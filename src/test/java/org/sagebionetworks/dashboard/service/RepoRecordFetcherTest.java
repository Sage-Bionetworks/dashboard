package org.sagebionetworks.dashboard.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class RepoRecordFetcherTest {

    @Test
    public void test() {

        // Mock 400 stacks
        List<String> stackList = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            String stack = String.format("%3d", i);
            stackList.add(stack);
        }
        RepoStackFetcher stackFetcher = mock(RepoStackFetcher.class);
        when(stackFetcher.getRecentStacks(30)).thenReturn(stackList);

        // Each stack gets 3 files.
        // File 1 is usual
        String key = UUID.randomUUID().toString();
        S3ObjectSummary summary = new S3ObjectSummary();
        summary.setKey(key);
        List<S3ObjectSummary> summaryList = new ArrayList<>();
        summaryList.add(summary);
        // File 2 is "rolling" and should be ignored
        key = UUID.randomUUID().toString() + "-rolling";
        summary = new S3ObjectSummary();
        summary.setKey(key);
        summaryList.add(summary);
        // File 3 is the last and should set the marker
        key = UUID.randomUUID().toString();
        String marker = key;
        summary = new S3ObjectSummary();
        summary.setKey(key);
        summaryList.add(summary);

        ObjectListing objListing = mock(ObjectListing.class);
        when(objListing.getObjectSummaries()).thenReturn(summaryList);
        AmazonS3 s3 = mock(AmazonS3.class);
        when(s3.listObjects(any(ListObjectsRequest.class))).thenReturn(objListing);

        RepoRecordFetcher fetcher = new RepoRecordFetcher();
        ReflectionTestUtils.setField(fetcher, "repoStackFetcher", stackFetcher, RepoStackFetcher.class);
        ReflectionTestUtils.setField(fetcher, "s3", s3, AmazonS3.class);

        List<String> files = fetcher.getBatch();
        // Should get back a full batch of 300 files
        assertEquals(300, files.size());
        @SuppressWarnings("unchecked")
        Map<String, String> markerMap = (Map<String, String>)ReflectionTestUtils.getField(fetcher, "stackMarkerMap");
        // Should cut off at stack 299
        assertTrue(markerMap.containsKey("299"));
        assertFalse(markerMap.containsKey("300"));
        // Should stop at stack 150
        // Going down from 299 to 150, we have 150 stacks with each stack has 2 valid files. A total of 300 files.
        assertTrue(markerMap.containsKey("150"));
        assertFalse(markerMap.containsKey("149"));
        // Should have the correct marker
        assertEquals(marker, markerMap.get("299"));
        assertEquals(marker, markerMap.get("175"));
    }
}
