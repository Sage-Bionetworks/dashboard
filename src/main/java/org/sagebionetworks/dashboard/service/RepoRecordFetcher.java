package org.sagebionetworks.dashboard.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * Fetches Synapse access log files batch by batch.
 */
@Service("repoRecordFetcher")
public class RepoRecordFetcher {

    private Logger logger = LoggerFactory.getLogger(RepoRecordFetcher.class);

    /** The max number of access log files in one batch. */
    private static final int BATCH_SIZE = 200;

    /** How many stacks to keep track of. It covers more than 7 months of data with 30 stacks. */
    private static final int STACK_COUNT = 30;

    /** Ignore stack numbers above the cutoff. */
    private static final long STACK_CUTOFF = 300L;

    @Resource
    private RepoStackFetcher repoStackFetcher;

    // For each stack prefix, record the last marker used
    // so that next batch for the same stack will start from the marker
    private final Map<String, String> stackMarkerMap;

    private final AmazonS3 s3;

    public RepoRecordFetcher() {
        stackMarkerMap = new HashMap<>();
        s3 = ServiceContext.getS3Client();
    }

    public List<String> getBatch() {

        logger.info("Getting a new batch...");
        final long start = System.currentTimeMillis();
        final List<String> batch = new ArrayList<>();
        // Get the last 30 stacks
        final List<String> stacks = repoStackFetcher.getRecentStacks(STACK_COUNT);
        // Start from the most recent stack
        final int stackCount = stacks.size();
        for (int i = stackCount - 1; i >= 0; i--) {
            final String stack = stacks.get(i);
            if (isValidStack(stack)) {
                fillBatchForStack(stack, batch);
                if (batch.size() >= BATCH_SIZE) {
                    return batch;
                }
            }
        }
        final long end = System.currentTimeMillis();
        logger.info("One batch[size=" + batch.size() + "] has been fetched in "
                + Long.toString(end - start) + " milliseconds.");
        return batch;
    }

    private void fillBatchForStack(final String stack, final List<String> batch) {

        logger.info("Filling the batch for stack " + stack + ".");
        final String bucket = ServiceContext.getBucket();
        ListObjectsRequest request = new ListObjectsRequest()
                .withBucketName(bucket)
                .withPrefix(stack)
                .withMaxKeys(BATCH_SIZE - batch.size());

        { // Use the marker to skip files already processed
            final String marker = stackMarkerMap.get(stack);
            if (marker != null) {
                request.setMarker(marker);
            }
        }

        final ObjectListing objListing  = s3.listObjects(request);

        final String newMarker = fillBatch(objListing, batch);
        if (newMarker != null) {
            stackMarkerMap.put(stack, newMarker);
            logger.info("Batch filled for stack " + stack + ". The stack now has a new marker "
                    + newMarker + ".");
        } else {
            logger.info("0 files are added to the batch and the stack marker is not updated.");
        }
    }

    /**
     * Recursively fills the batch till BATCH_SIZE and returns the marker (which can be null).
     */
    private String fillBatch(final ObjectListing objListing, final List<String> batch) {

        String marker = null;
        for (S3ObjectSummary obj : objListing.getObjectSummaries()) {
            final String key = obj.getKey();
            if (isValidKey(key)) {
                logger.info("Adding key " + key + " to the batch.");
                batch.add(key);
                marker = key;
            }
        }

        if (objListing.isTruncated()) {
            final ObjectListing nextObjListing = s3.listNextBatchOfObjects(objListing);
            final String newMarker = fillBatch(nextObjListing, batch);
            if (newMarker != null) {
                marker = newMarker;
            }
        }

        return marker;
    }

    private boolean isValidKey(final String key) {
        return !key.toLowerCase().contains("rolling");
    }

    private boolean isValidStack(String stack) {
        // Remove the trailing '/'
        if (stack.endsWith("/")) {
            stack = stack.substring(0, stack.length() - 1);
        }
        long stackNumber = Long.parseLong(stack);
        return (stackNumber < STACK_CUTOFF);
    }
}
