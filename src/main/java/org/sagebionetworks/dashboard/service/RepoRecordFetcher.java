package org.sagebionetworks.dashboard.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Service("repoRecordFetcher")
public class RepoRecordFetcher {

    private static final int BATCH_SIZE = 300;

    private final AmazonS3 s3;
    private String lastMarker = null;

    public RepoRecordFetcher() {
        s3 = ServiceContext.getS3Client();
    }

    RepoRecordFetcher(AmazonS3 s3) {
        this.s3 = s3;
    }

    public List<String> getBatch() {

        List<String> files = new ArrayList<>();
        final String bucket = ServiceContext.getBucket();
        ObjectListing objListing = null;
        if (lastMarker == null || lastMarker.isEmpty()) {
            objListing = s3.listObjects(bucket);
        } else {
            ListObjectsRequest request = new ListObjectsRequest()
                    .withBucketName(bucket)
                    .withMarker(lastMarker)
                    .withMaxKeys(BATCH_SIZE);
            objListing = s3.listObjects(request);
        }
        fillBatch(files, objListing);
        return files;
    }

    private void fillBatch(final List<String> files, final ObjectListing objListing) {

        for (S3ObjectSummary obj : objListing.getObjectSummaries()) {
            final String key = obj.getKey();
            if (isValidKey(key)) {
                lastMarker = key;
                files.add(key);
                if (files.size() >= BATCH_SIZE) {
                    return;
                }
            }
        }

        if (objListing.isTruncated()) {
            ObjectListing nextObjListing = s3.listNextBatchOfObjects(objListing);
            fillBatch(files, nextObjListing);
        }
    }

    private boolean isValidKey(String key) {
        String[] parts = key.split("/");
        if (parts.length == 0) {
            return false;
        }
        if (parts.length == 3) {
            long stack = Long.parseLong(parts[0]);
            if (stack > 299) {
                // Ignore stacks like 999
                return false;
            }
        }
        if (parts[parts.length - 1].toLowerCase().contains("rolling")) {
            // Ignore rolling files
            return false;
        }
        return true;
    }
}
