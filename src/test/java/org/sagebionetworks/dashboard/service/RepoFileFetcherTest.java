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

public class RepoFileFetcherTest {

    @Test
    public void test() {

        // Mock 200 folders
        List<String> folderList = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            String folder = String.format("%03d", i);
            folderList.add(folder);
        }
        RepoFolderFetcher folderFetcher = mock(RepoFolderFetcher.class);
        when(folderFetcher.getRecentFolders(200)).thenReturn(folderList);

        // Each folder gets 3 files -- 600 files in total
        // File 1 is the usual
        String key = UUID.randomUUID().toString();
        S3ObjectSummary summary = new S3ObjectSummary();
        summary.setKey(key);
        summary.setSize(10000000); // 10 MB
        List<S3ObjectSummary> summaryList = new ArrayList<>();
        summaryList.add(summary);
        // File 2 is "rolling" and should be ignored
        key = UUID.randomUUID().toString() + "-rolling.csv.gz";
        summary = new S3ObjectSummary();
        summary.setKey(key);
        summary.setSize(10000000); // 10 MB
        summaryList.add(summary);
        // File 3 is the last and should set the marker
        key = UUID.randomUUID().toString();
        final String marker = key;
        summary = new S3ObjectSummary();
        summary.setKey(key);
        summary.setSize(10000000); // 10 MB
        summaryList.add(summary);

        ObjectListing objListing = mock(ObjectListing.class);
        when(objListing.getObjectSummaries()).thenReturn(summaryList);
        AmazonS3 s3 = mock(AmazonS3.class);
        when(s3.listObjects(any(ListObjectsRequest.class))).thenReturn(objListing);
        ObjectListing nextObjListing = mock(ObjectListing.class);
        when(nextObjListing.getObjectSummaries()).thenReturn(new ArrayList<S3ObjectSummary>());
        when(s3.listNextBatchOfObjects(objListing)).thenReturn(nextObjListing);

        RepoFileFetcher fetcher = new RepoFileFetcher();
        ReflectionTestUtils.setField(fetcher, "repoFolderFetcher", folderFetcher, RepoFolderFetcher.class);
        ReflectionTestUtils.setField(fetcher, "s3", s3, AmazonS3.class);

        List<String> files = fetcher.nextBatch();
        // Should get back a full batch of 20 files as each file is 10 MB and the max total size per batch is 200 MB
        assertEquals(20, files.size());
        @SuppressWarnings("unchecked")
        Map<String, String> markerMap = (Map<String, String>)ReflectionTestUtils.getField(fetcher, "folderMarkerMap");
        // Each folder gets 2 files (file 1 and 3) into the batch and there are 20 files in the batch
        // We should cut off at folder 10
        assertTrue(markerMap.containsKey("009"));
        assertFalse(markerMap.containsKey("010"));
        // Each folder should have the correct marker
        for (int i = 0; i < 10; i++) {
            assertEquals(marker, markerMap.get(String.format("%03d", i)));
        }
    }
}
