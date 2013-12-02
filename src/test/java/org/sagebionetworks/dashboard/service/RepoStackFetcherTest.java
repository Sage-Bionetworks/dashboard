package org.sagebionetworks.dashboard.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;

public class RepoStackFetcherTest {

    @Test
    public void test() {

        List<String> prefixes = Arrays.asList("1", "2", "3");
        ObjectListing objListing = mock(ObjectListing.class);
        when(objListing.getCommonPrefixes()).thenReturn(prefixes);

        AmazonS3 s3 = mock(AmazonS3.class);
        when(s3.listObjects(any(ListObjectsRequest.class))).thenReturn(objListing);

        RepoStackFetcher fetcher = new RepoStackFetcher();
        ReflectionTestUtils.setField(fetcher, "s3", s3, AmazonS3.class);

        List<String> results = fetcher.getLiveStacks();
        assertNotNull(results);
        assertEquals(3, results.size());
        assertEquals("1", results.get(0));
        assertEquals("2", results.get(1));
        assertEquals("3", results.get(2));

        prefixes = Arrays.asList("1", "2", "3", "4", "5");
        when(objListing.getCommonPrefixes()).thenReturn(prefixes);
        results = fetcher.getLiveStacks();
        assertNotNull(results);
        assertEquals(3, results.size());
        assertEquals("3", results.get(0));
        assertEquals("4", results.get(1));
        assertEquals("5", results.get(2));

        prefixes = Arrays.asList("1", "2");
        when(objListing.getCommonPrefixes()).thenReturn(prefixes);
        results = fetcher.getLiveStacks();
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("1", results.get(0));
        assertEquals("2", results.get(1));
    }
}
