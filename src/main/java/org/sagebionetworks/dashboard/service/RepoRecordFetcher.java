package org.sagebionetworks.dashboard.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Service("repoRecordFetcher")
public class RepoRecordFetcher {

    private static final String PROPERTY_PROD = "prod";
    private static final String BUCKET_PROD = "prod.access.record.sagebase.org";
    private static final String BUCKET_DEV = "dev.access.record.sagebase.org";
    private static final int BATCH_SIZE = 100;

    private final AmazonS3 s3;
    private String lastPrefix = null;

    public RepoRecordFetcher() {
        this.s3 = AwsS3Client.S3_CLIENT;
    }

    RepoRecordFetcher(AmazonS3 s3) {
        this.s3 = s3;
    }

    public List<S3Object> getBatch() {

        List<S3Object> files = new ArrayList<>();
        final String bucket = getBucket();
        ObjectListing objListing = null;
        if (lastPrefix == null || lastPrefix.isEmpty()) {
            objListing = s3.listObjects(bucket);
        } else {
            objListing = s3.listObjects(bucket, lastPrefix);
        }
        fillBatch(files, objListing);
        return files;
    }

    private void fillBatch(List<S3Object> files, ObjectListing objListing) {

        if (files.size() > BATCH_SIZE) {
            return;
        }

        List<S3ObjectSummary> summeries = objListing.getObjectSummaries();
        if (summeries.isEmpty()) {
            return;
        }

        final String bucket = getBucket();
        for (S3ObjectSummary obj : objListing.getObjectSummaries()) {
            final String key = obj.getKey();
            if (!key.toLowerCase().contains("rolling")) {
                final int i = key.lastIndexOf('/');
                if (i <= 0) {
                    lastPrefix = null;
                } else {
                    lastPrefix = key.substring(0, i);
                }
                S3Object file = s3.getObject(bucket, key);
                files.add(file);
            }
        }

        objListing = s3.listNextBatchOfObjects(objListing);
        fillBatch(files, objListing);
    }

    private String getBucket() {
        if (isProd()) {
            return BUCKET_PROD;
        }
        return BUCKET_DEV;
    }

    private boolean isProd() {
        return Boolean.parseBoolean(System.getProperty(PROPERTY_PROD));
    }
}
