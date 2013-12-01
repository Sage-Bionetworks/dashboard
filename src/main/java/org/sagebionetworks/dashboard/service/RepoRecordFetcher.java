package org.sagebionetworks.dashboard.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Service("repoRecordFetcher")
public class RepoRecordFetcher {

    private static final int BATCH_SIZE = 300;

    private final AmazonS3 s3;
    private String lastPrefix = null;

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
        if (lastPrefix == null || lastPrefix.isEmpty()) {
            objListing = s3.listObjects(bucket);
        } else {
            objListing = s3.listObjects(bucket, lastPrefix);
        }
        fillBatch(files, objListing);
        return files;
    }

    private void fillBatch(final List<String> files, final ObjectListing objListing) {

        if (files.size() >= BATCH_SIZE) {
            return;
        }

        List<S3ObjectSummary> summeries = objListing.getObjectSummaries();
        if (summeries.isEmpty()) {
            return;
        }

        for (S3ObjectSummary obj : objListing.getObjectSummaries()) {
            final String key = obj.getKey();
            if (!key.toLowerCase().contains("rolling")) {
                final int i = key.lastIndexOf('/');
                if (i <= 0) {
                    lastPrefix = null;
                } else {
                    lastPrefix = key.substring(0, i);
                }
                files.add(key);
                if (files.size() >= BATCH_SIZE) {
                    return;
                }
            }
        }

        ObjectListing nextObjListing = s3.listNextBatchOfObjects(objListing);
        fillBatch(files, nextObjListing);
    }
}
