package org.sagebionetworks.dashboard.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;

@Service("repoStackFetcher")
public class RepoStackFetcher {

    private AmazonS3 s3;

    public RepoStackFetcher() {
        s3 = ServiceContext.getS3Client();
    }

    /**
     * Gets the most recent stacks as S3 prefixes in alphabetical order.
     *
     * @param stackCount How many stacks to get
     */
    public List<String> getRecentStacks(final int stackCount) {

        if (stackCount < 0) {
            throw new IllegalArgumentException("Stack count must be >= 0.");
        }

        final String bucket = ServiceContext.getBucket();
        ListObjectsRequest request = new ListObjectsRequest()
                .withBucketName(bucket)
                .withDelimiter("/");
        ObjectListing objListing = s3.listObjects(request);
        List<String> prefixes = objListing.getCommonPrefixes();
        int to = prefixes.size();
        int from = to - stackCount;
        from = from < 0 ? 0 : from;
        return Collections.unmodifiableList(prefixes.subList(from, to));
    }
}
