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

    /** How many stacks to keep track of. */
    private static final int STACK_COUNT = 30;

    /** Ignore stack numbers above the cutoff. */
    private static final long STACK_CUTOFF = 300L;

    @Resource
    private RepoStackFetcher repoStackFetcher;

    // For each stack prefix, record the last marker used
    // so that next batch for the same stack will start from the marker
    private final Map<String, String> stackMarkerMap;

    private AmazonS3 s3;

    public RepoRecordFetcher() {
        stackMarkerMap = new HashMap<>();
        s3 = ServiceContext.getS3Client();
    }

    public List<String> getBatch() {

        logger.info("Getting a new batch...");
        final List<String> batch = new ArrayList<>();
        // Get the last 30 stacks
        final List<String> stacks = repoStackFetcher.getRecentStacks(STACK_COUNT);
        // Start from the most recent stack
        final int stackCount = stacks.size();
        for (int i = stackCount - 1; i >= 0; i--) {
            final String stack = stacks.get(i);
            if (isValidStack(stack)) {
                fillBatch(stack, batch);
                if (batch.size() >= BATCH_SIZE) {
                    return batch;
                }
            }
        }
        logger.info("One batch[size=" + batch.size() + "] has been retrieved.");
        return batch;
    }

    private void fillBatch(final String stack, final List<String> batch) {

        logger.info("Filling the batch[size=" + batch.size() + "] for stack " + stack + ".");
        final String bucket = ServiceContext.getBucket();
        ListObjectsRequest request = new ListObjectsRequest()
                .withBucketName(bucket)
                .withPrefix(stack)
                .withMaxKeys(BATCH_SIZE);

        String marker = stackMarkerMap.get(stack);
        if (marker != null) {
            request.setMarker(marker);
        }

        ObjectListing objListing  = s3.listObjects(request);

        marker = fillBatch(objListing, batch);
        logger.info("Batch[size=" + batch.size() + "] filled for stack " + stack +
                " with a new marker " + marker + ". Null marker will NOT be updated.");
        if (marker != null) {
            stackMarkerMap.put(stack, marker);
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
                if (batch.size() >= BATCH_SIZE) {
                    return marker;
                }
            }
        }

        if (objListing.isTruncated()) {
            final ObjectListing nextObjListing = s3.listNextBatchOfObjects(objListing);
            String newMarker = fillBatch(nextObjListing, batch);
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
