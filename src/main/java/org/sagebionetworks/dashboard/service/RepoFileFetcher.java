package org.sagebionetworks.dashboard.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.FileStatusDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * Fetches Synapse access record files batch by batch.
 */
@Service("repoFileFetcher")
public class RepoFileFetcher {

    private Logger logger = LoggerFactory.getLogger(RepoFileFetcher.class);

    /**  How many days to fetch. */
    private static final int DAYS = 200;
    /** The max number of bytes in one batch. */
    private static final long BATCH_SIZE = 6L * 1000L * 1000L; // 6 MB per batch

    @Resource
    private RepoFolderFetcher repoFolderFetcher;

    @Resource
    private FileStatusDao fileStatusDao;

    // For each folder, record the last marker used so that
    // the next batch for the same folder will start from the marker
    private final Map<String, String> folderMarkerMap;

    private final AmazonS3 s3;

    public RepoFileFetcher() {
        folderMarkerMap = new ConcurrentHashMap<>();
        s3 = ServiceContext.getS3Client();
    }

    /**
     * Gets the next batch of files. The batch includes files that
     * are NOT in previous batches.
     */
    public List<String> nextBatch() {
        final String msg = "Computing the next batch of files to fetch";
        logger.info(msg + "...");
        long start = System.currentTimeMillis();
        final List<String> batch = new ArrayList<>();
        // Get all the folders of the last 200 days
        final List<String> folderList = repoFolderFetcher.getRecentFolders(DAYS);
        long quota = BATCH_SIZE;
        for (String folder : folderList) {
            quota = fillBatch(folder, batch, quota);
            if (quota <= 0) {
                break;
            }
        }
        long elapse = System.currentTimeMillis() - start;
        logger.info(msg + " -- done in " + elapse + " milliseconds. A batch of "
                + batch.size() + " files is being returned.");
        return batch;
    }

    /**
     * @return Remaining quota
     */
    private long fillBatch(final String folder, List<String> batch, long quota) {

        logger.info("Filling the batch for folder " + folder + "...");

        String bucket = ServiceContext.getBucket();
        ListObjectsRequest request = new ListObjectsRequest()
                .withBucketName(bucket)
                .withPrefix(folder);

        // Use the marker to skip files already fetched in previous batches
        String marker = folderMarkerMap.get(folder);
        if (marker != null) {
            request.setMarker(marker);
        }

        ObjectListing objListing  = s3.listObjects(request);
        do {
            for (S3ObjectSummary obj : objListing.getObjectSummaries()) {
                final String key = obj.getKey();
                if (isValidKey(key)) {
                    // Make sure the file hasn't been processed yet
                    if (!fileStatusDao.isCompleted(key) && !fileStatusDao.isFailed(key)) {
                        batch.add(key);
                        quota = quota - obj.getSize();
                        logger.info("Added key " + key + " to the batch.");
                    }
                    // Update the marker so that we visit new files on the next batch
                    folderMarkerMap.put(folder, key);
                    // Exit early when no more quota left
                    if (quota <= 0) {
                        return quota;
                    }
                }
            }
            objListing = s3.listNextBatchOfObjects(objListing);
        } while (objListing.getObjectSummaries().size() > 0);
        return quota;
    }

    private boolean isValidKey(final String key) {
        return key != null && !key.toLowerCase().endsWith("rolling.csv.gz");
    }
}
