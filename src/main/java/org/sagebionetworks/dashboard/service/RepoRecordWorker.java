package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.FileStatusDao;
import org.sagebionetworks.dashboard.dao.LockDao;
import org.springframework.stereotype.Service;

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

        List<S3Object> batch = repoRecordFetcher.getBatch();
        for (S3Object file : batch) {
            final String key = file.getKey();
            final String etag = lockDao.acquire(key);
            if (etag == null) {
                continue;
            }
            try {
                if (fileStatusDao.isCompleted(key) || fileStatusDao.isFailed(key)) {
                    // Already processed
                    continue;
                }
                updateService.load(file.getObjectContent(), key, new UpdateCallback() {
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
