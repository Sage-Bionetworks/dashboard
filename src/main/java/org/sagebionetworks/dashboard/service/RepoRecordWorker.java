package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.context.DashboardContext;
import org.sagebionetworks.dashboard.dao.FileStatusDao;
import org.sagebionetworks.dashboard.dao.LockDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

@Service("repoRecordWorker")
public class RepoRecordWorker {

    private final Logger logger = LoggerFactory.getLogger(RepoRecordWorker.class);

    @Resource
    private DashboardContext dashboardContext;

    @Resource
    private AmazonS3 s3Client;

    @Resource
    private FileStatusDao fileStatusDao;

    @Resource
    private LockDao lockDao;

    @Resource
    private RepoUpdateService repoUpdateService;

    @Resource
    private RepoFileFetcher repoFileFetcher;

    public void doWork() {
        final String bucket = dashboardContext.getAccessRecordBucket();
        List<String> batch = repoFileFetcher.nextBatch();
        for (final String key : batch) {
            updateFile(bucket, key, 0);
        }
        try {
            logger.info("Pausing for 30 seconds...");
            Thread.sleep(30000);
            logger.info("Done pausing.");
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
    }

    public void updateFile(final String bucket, final String key, final int startingLine) {

        // Try to acquire a lock on the file
        final String etag = lockDao.acquire(key);
        if (etag == null) {
            logger.info("Failed to acquire lock for file " + key + ". Skipping it...");
            return;
        }
        if (fileStatusDao.isCompleted(key)) {
            lockDao.release(key, etag);
            logger.info("File " + key + " already processed. Skipping it...");
            return;
        }

        // Read the file to update the metrics
        S3Object file = s3Client.getObject(bucket, key);
        try {
            repoUpdateService.update(file.getObjectContent(), key, startingLine, new UpdateCallback() {
                @Override
                public void call(UpdateResult result) {
                    if (UpdateStatus.SUCCEEDED.equals(result.getStatus())) {
                        fileStatusDao.setCompleted(key);
                    } else {
                        fileStatusDao.setFailed(key, result.getLineCount());
                    }
                }
            });
        } finally {
            try {
                file.close();
                lockDao.release(key, etag);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
