package org.sagebionetworks.dashboard.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class RepoRecordFetcherTest {

    @Before
    public void before() {
        System.setProperty("prod", "true");
    }

    @After
    public void after() {
        System.setProperty("prod", "false");
    }

    @Test
    public void test() {

        final String bucket = "prod.access.record.sagebase.org";
        AmazonS3 s3 = mock(AmazonS3.class);

        // Mock one page of 200 files with 50 rolling
        // 1) Should get back 150 files
        // 2) Should stop filling the batch as it is over 100
        // 3) Should have the correct lastPrefix
        List<S3ObjectSummary> summaryList = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            S3ObjectSummary summary = new S3ObjectSummary();
            String key = String.format("%3d", i) + "/" + String.format("%3d", i);
            if ((i % 4) == 0) {
                key = key + "rolling";
            }
            summary.setKey(key);
            summaryList.add(summary);
            when(s3.getObject(bucket, key)).thenReturn(mock(S3Object.class));
        }
        ObjectListing objListing = mock(ObjectListing.class);
        when(objListing.getObjectSummaries()).thenReturn(summaryList);
        when(s3.listObjects("prod.access.record.sagebase.org")).thenReturn(objListing);

        RepoRecordFetcher fetcher = new RepoRecordFetcher(s3);
        List<S3Object> files = fetcher.getBatch();
        assertEquals(150, files.size());
        assertEquals("199", (String)ReflectionTestUtils.getField(fetcher, "lastPrefix"));

        // Mock the next page of 20 files with 5 rolling
        summaryList = new ArrayList<>();
        for (int i = 200; i < 220; i++) {
            S3ObjectSummary summary = new S3ObjectSummary();
            String key = String.format("%3d", i) + "/" + String.format("%3d", i);
            if ((i % 4) == 0) {
                key = key + "rolling";
            }
            summary.setKey(key);
            summaryList.add(summary);
            when(s3.getObject(bucket, key)).thenReturn(mock(S3Object.class));
        }
        objListing = mock(ObjectListing.class);
        when(objListing.getObjectSummaries()).thenReturn(summaryList);
        when(s3.listObjects("prod.access.record.sagebase.org", "199")).thenReturn(objListing);
        // No more page
        ObjectListing emptyList = mock(ObjectListing.class);
        when(emptyList.getObjectSummaries()).thenReturn(new ArrayList<S3ObjectSummary>());
        when(s3.listNextBatchOfObjects(objListing)).thenReturn(emptyList);
        files = fetcher.getBatch();
        assertEquals(15, files.size());
        assertEquals("219", (String)ReflectionTestUtils.getField(fetcher, "lastPrefix"));
    }
}
