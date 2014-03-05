package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

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
    private FileStatusDao fileStatusDao;

    @Resource
    private LockDao lockDao;

    @Resource
    private RepoUpdateService repoUpdateService;

    @Resource
    private RepoFileFetcher repoFileFetcher;

    public void doWork() {

        final AmazonS3 s3 = ServiceContext.getS3Client();
        final String bucket = ServiceContext.getBucket();
        List<String> batch = repoFileFetcher.nextBatch();
        for (final String key : batch) {

            final String etag = lockDao.acquire(key);
            if (etag == null) {
                // Fail to acquire lock
                logger.info("Failed to acquire lock for file " + key + ". Skipping it...");
                continue;
            }
            if (fileStatusDao.isCompleted(key) || fileStatusDao.isFailed(key)) {
                // Already processed
                lockDao.release(key, etag);
                logger.info("File " + key + " already processed. Skipping it...");
                continue;
            }

            S3Object file = s3.getObject(bucket, key);
            try {
                repoUpdateService.update(file.getObjectContent(), key, new UpdateCallback() {
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
}
