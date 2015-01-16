package org.sagebionetworks.dashboard.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.dashboard.config.DashboardConfig;
import org.sagebionetworks.dashboard.dao.FileStatusDao;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.bytecode.opencsv.CSVWriter;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RepoRecordWorkerTest {

    @Resource
    private DashboardConfig dashboardConfig;

    @Resource
    private AmazonS3 s3Client;

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource
    private FileStatusDao fileStatusDao;

    @Resource
    private RepoRecordWorker repoRecordWorker;

    @Resource
    private RepoRepairWorker repoRepairWorker;

    private String keySuccess;
    private String keyFailure;

    @Before
    public void before() throws Exception {
        assertNotNull(dashboardConfig);
        assertNotNull(s3Client);
        assertNotNull(redisTemplate);
        assertNotNull(fileStatusDao);
        assertNotNull(repoRecordWorker);
        clearRedis();
        cleanS3();
        String[] success = {
                "syn123", "63", "1383058860156", null,
                "repo-prod.sagebase.org", "18111", null, null,
                "ed39b97b", null, "/repo/v1/entity/syn123", "22222", null,
                "2013-10-29", "GET", "628c13a", "18", "prod", "true", "202"
            };
        keySuccess = putS3Object(success);
        assertNotNull(keySuccess);
        String[] failure = {"This can't be parsed."};
        keyFailure = putS3Object(failure);
        assertNotNull(keyFailure);
    }

    @After
    public void after() {
        clearRedis();
        final String bucket = dashboardConfig.getAccessRecordBucket();
        if (keySuccess != null) {
            s3Client.deleteObject(bucket, keySuccess);
        }
        if (keyFailure != null) {
            s3Client.deleteObject(bucket, keyFailure);
        }
    }

    @Test
    public void test() {
        repoRecordWorker.doWork();
        assertTrue(fileStatusDao.isCompleted(keySuccess));
        assertFalse(fileStatusDao.isFailed(keySuccess));
        repoRepairWorker.doWork();
    }

    private void clearRedis() {
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            redisTemplate.delete(key);
            assertFalse(redisTemplate.hasKey(key));
        }
    }

    /**
     * Clean the dev S3 bucket of files older than 1 day. When other tests working on the same bucket
     * fail to clean, we do not want to do too much extra work.
     */
    private void cleanS3() throws IOException {
        final String bucket = dashboardConfig.getAccessRecordBucket();
        final List<KeyVersion> toDelete = new ArrayList<>();
        final DateTime now = DateTime.now(DateTimeZone.UTC);
        final Date yesterday = now.minusDays(1).toDate();
        ObjectListing listing = null;
        do {
            if (listing == null) {
                listing = s3Client.listObjects(bucket);
            } else {
                listing = s3Client.listNextBatchOfObjects(listing);
            }
            for (S3ObjectSummary obj : listing.getObjectSummaries()) {
                Date date = obj.getLastModified();
                if (date.before(yesterday)) {
                    toDelete.add(new KeyVersion(obj.getKey()));
                }
            }
        }
        while (listing.isTruncated());
        // Clean in multiple batches; each batch at most 300 files.
        if (toDelete.size() > 0) {
            int from = 0;
            int to = 300;
            while (from < toDelete.size()) {
                to = to > toDelete.size() ? toDelete.size() : to;
                s3Client.deleteObjects(new DeleteObjectsRequest(bucket)
                        .withKeys(toDelete.subList(from, to)));
                from = to;
                to = to + 300;
            }
        }
    }

    private String putS3Object(String[] line) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzos = new GZIPOutputStream(baos);
        OutputStreamWriter osw = new OutputStreamWriter(gzos, StandardCharsets.UTF_8);
        CSVWriter cw = new CSVWriter(osw);
        cw.writeNext(line);
        cw.close();
        byte[] bytes = baos.toByteArray();

        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentEncoding("gzip");
        metadata.setContentType("application/x-gzip");
        metadata.setContentLength(bytes.length);

        final String bucket = dashboardConfig.getAccessRecordBucket();
        final String key = "000000023/2014-02-28/" + UUID.randomUUID().toString() + ".csv.gz";
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        s3Client.putObject(bucket, key, bais, metadata);
        bais.close();

        return key;
    }
}
