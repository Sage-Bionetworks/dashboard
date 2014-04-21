package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.context.DashboardContext;
import org.sagebionetworks.dashboard.dao.FileStatusDao;
import org.sagebionetworks.dashboard.model.FileFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("repoRepairWorker")
public class RepoRepairWorker {

    private final Logger logger = LoggerFactory.getLogger(RepoRepairWorker.class);

    @Resource
    private DashboardContext dashboardContext;

    @Resource
    private FileStatusDao fileStatusDao;

    @Resource
    private RepoRecordWorker repoRecordWorker;

    public void doWork() {
        final String bucket = dashboardContext.getAccessRecordBucket();
        final List<FileFailure> failures = fileStatusDao.getFailures();
        for (FileFailure failure : failures) {
            final String key = failure.getFile();
            final int line = failure.getLineNumber();
            logger.info("Repairing " + key + " starting from line " + line);
            repoRecordWorker.updateFile(bucket, key, line);
        }
    }
}
