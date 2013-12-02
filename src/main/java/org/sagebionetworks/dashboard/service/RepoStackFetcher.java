package org.sagebionetworks.dashboard.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;

@Service("repoRecordFetcher")
public class RepoStackFetcher {

    /** How many live stacks to keep track of. */
    private static final int STACK_COUNT = 3;

    private AmazonS3 s3;

    public RepoStackFetcher() {
        s3 = ServiceContext.getS3Client();
    }

    /**
     * Gets the live stacks as S3 prefixes in alphabetical order. 
     */
    public List<String> getLiveStacks() {
        final String bucket = ServiceContext.getBucket();
        ListObjectsRequest request = new ListObjectsRequest()
                .withBucketName(bucket)
                .withDelimiter("/");
        ObjectListing objListing = s3.listObjects(request);
        List<String> prefixes = objListing.getCommonPrefixes();
        int to = prefixes.size();
        int from = to - STACK_COUNT;
        from = from < 0 ? 0 : from;
        return Collections.unmodifiableList(prefixes.subList(from, to));
    }
}
