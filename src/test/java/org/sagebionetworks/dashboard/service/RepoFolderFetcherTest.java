package org.sagebionetworks.dashboard.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.dashboard.context.DashboardContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RepoFolderFetcherTest {

    @Resource
    private DashboardContext dashboardContext;

    @Resource
    private AmazonS3 s3Client;

    @Resource
    private RepoFolderFetcher repoFolderFetcher;

    private List<String> keyList;
    private String stackStart;
    private String stackEnd;

    @Before
    public void before() throws Exception {

        assertNotNull(s3Client);
        assertNotNull(repoFolderFetcher);

        // Clear S3 objects under this test.
        final String bucket = dashboardContext.getAccessRecordBucket();
        final String prefix = getClass().getSimpleName();
        ObjectListing objListing = s3Client.listObjects(bucket, prefix);
        for (S3ObjectSummary obj : objListing.getObjectSummaries()) {
            s3Client.deleteObject(bucket, obj.getKey());
        }

        // Create test objects in S3
        stackStart = prefix + "0010";
        stackEnd = prefix + "0100";
        keyList = new ArrayList<String>();
        // Stack 9 should be excluded even if it has a newer date
        keyList.add(prefix + "0009/2013-11-05/" + UUID.randomUUID().toString());
        // Stack 10 should be included
        keyList.add(prefix + "0010/2013-11-03/" + UUID.randomUUID().toString());
        keyList.add(prefix + "0010/2013-11-03/" + UUID.randomUUID().toString());
        keyList.add(prefix + "0010/2013-11-02/" + UUID.randomUUID().toString());
        // Stack 20 should be included and get sorted before stack 10
        keyList.add(prefix + "0020/2013-11-03/" + UUID.randomUUID().toString());
        // Stack 21 should be included but with an older date should get sorted after stack 10
        keyList.add(prefix + "0021/2013-10-21/" + UUID.randomUUID().toString());
        // Stack 15 should be included
        keyList.add(prefix + "0015/2013-09-16/" + UUID.randomUUID().toString());
        // Stack 999 should be excluded
        keyList.add(prefix + "0999/2013-10-21/" + UUID.randomUUID().toString());
        for (String key : keyList) {
            s3PutObject(key);
        }
    }

    @After
    public void after() throws Exception {
        for (String key : keyList) {
            s3Client.deleteObject(dashboardContext.getAccessRecordBucket(), key);
        }
    }

    @Test
    public void test() throws Exception {
        // Cut off at 3 days should remove the last one "0015/2013-09-16/"
        List<String> folderList = repoFolderFetcher.getRecentFolders(3, stackStart, stackEnd);
        assertNotNull(folderList);
        assertEquals(4, folderList.size());
        final String prefix = getClass().getSimpleName();
        assertEquals(prefix + "0020/2013-11-03/", folderList.get(0).toString());
        assertEquals(prefix + "0010/2013-11-03/", folderList.get(1).toString());
        assertEquals(prefix + "0010/2013-11-02/", folderList.get(2).toString());
        assertEquals(prefix + "0021/2013-10-21/", folderList.get(3).toString());
    }

    private void s3PutObject(String key) throws Exception {
        final byte[] bytes = "RepoFolderFetcherTest".getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/html");
        metadata.setContentLength(bytes.length);
        s3Client.putObject(dashboardContext.getAccessRecordBucket(), key, inputStream, metadata);
        inputStream.close();
    }
}
