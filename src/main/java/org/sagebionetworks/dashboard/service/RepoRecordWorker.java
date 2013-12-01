package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.FileStatusDao;
import org.sagebionetworks.dashboard.dao.LockDao;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

@Service("repoRecordWorker")
public class RepoRecordWorker {

    @Resource
    private FileStatusDao fileStatusDao;

    @Resource
    private LockDao lockDao;

    @Resource
    private UpdateService updateService;

    @Resource
    private RepoRecordFetcher repoRecordFetcher;

    public void doWork() {

        final AmazonS3 s3 = ServiceContext.getS3Client();
        final String bucket = ServiceContext.getBucket();
        List<String> batch = repoRecordFetcher.getBatch();
        for (final String key : batch) {

            final String etag = lockDao.acquire(key);
            if (etag == null) {
                // Fail to acquire lock
                continue;
            }
            if (fileStatusDao.isCompleted(key) || fileStatusDao.isFailed(key)) {
                // Already processed
                lockDao.release(key, etag);
                continue;
            }

            S3Object file = s3.getObject(bucket, key);
            try {
                updateService.update(file.getObjectContent(), key, new UpdateCallback() {
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
