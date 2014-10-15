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
import org.sagebionetworks.dashboard.config.DashboardConfig;
import org.sagebionetworks.dashboard.dao.FileStatusDao;
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

        // Each folder gets 4 files -- 800 files in total
        // File 1 is the usual
        String key = UUID.randomUUID().toString();
        S3ObjectSummary summary = new S3ObjectSummary();
        summary.setKey(key);
        summary.setSize(100000); // 100 KB
        List<S3ObjectSummary> summaryList = new ArrayList<>();
        summaryList.add(summary);
        // File 2 is "rolling" and should be ignored
        key = UUID.randomUUID().toString() + "-rolling.csv.gz";
        summary = new S3ObjectSummary();
        summary.setKey(key);
        summary.setSize(100000); // 100 KB
        summaryList.add(summary);
        // File 3 is the usual
        key = UUID.randomUUID().toString();
        summary = new S3ObjectSummary();
        summary.setKey(key);
        summary.setSize(100000); // 100 KB
        summaryList.add(summary);
        // File 4 is completed and should not be in the list
        // but it is the last and should set the marker
        final String key4 = UUID.randomUUID().toString();
        summary = new S3ObjectSummary();
        summary.setKey(key4);
        summary.setSize(100000); // 100 KB
        summaryList.add(summary);

        ObjectListing objListing = mock(ObjectListing.class);
        when(objListing.getObjectSummaries()).thenReturn(summaryList);
        AmazonS3 s3 = mock(AmazonS3.class);
        when(s3.listObjects(any(ListObjectsRequest.class))).thenReturn(objListing);
        ObjectListing nextObjListing = mock(ObjectListing.class);
        when(nextObjListing.getObjectSummaries()).thenReturn(new ArrayList<S3ObjectSummary>());
        when(s3.listNextBatchOfObjects(objListing)).thenReturn(nextObjListing);

        FileStatusDao fileStatusDao = mock(FileStatusDao.class);
        when(fileStatusDao.isCompleted(key4)).thenReturn(true);
        when(fileStatusDao.isFailed(key4)).thenReturn(false);

        DashboardConfig config = mock(DashboardConfig.class);
        when(config.getAccessRecordBucket()).thenReturn("bucket");

        RepoFileFetcher fetcher = new RepoFileFetcher();
        ReflectionTestUtils.setField(fetcher, "repoFolderFetcher", folderFetcher, RepoFolderFetcher.class);
        ReflectionTestUtils.setField(fetcher, "s3Client", s3, AmazonS3.class);
        ReflectionTestUtils.setField(fetcher, "fileStatusDao", fileStatusDao, FileStatusDao.class);
        ReflectionTestUtils.setField(fetcher, "dashboardConfig", config, DashboardConfig.class);

        List<String> files = fetcher.nextBatch();
        // Should get back a full batch of 90 files as each file is 100 KB and the max total size per batch is 6 MB
        assertEquals(90, files.size());
        @SuppressWarnings("unchecked")
        Map<String, String> markerMap = (Map<String, String>)ReflectionTestUtils.getField(fetcher, "folderMarkerMap");
        // Each folder gets 2 files (file 1 and 3) into the batch and there are 90 files in the batch
        // We should cut off at folder 45
        assertTrue(markerMap.containsKey("044"));
        assertFalse(markerMap.containsKey("045"));
        // Each folder should have the correct marker
        for (int i = 0; i < 44; i++) {
            assertEquals(key4, markerMap.get(String.format("%03d", i)));
        }
        assertEquals(key, markerMap.get(String.format("%03d", 44)));
    }
}
