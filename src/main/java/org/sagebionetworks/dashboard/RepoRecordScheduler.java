package org.sagebionetworks.dashboard;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.service.RepoRecordWorker;
import org.springframework.scheduling.annotation.Scheduled;

public class RepoRecordScheduler {

    @Resource
    private RepoRecordWorker repoRecordWorker;

    /**
     * Updates every 30 minutes.
     */
    @Scheduled(fixedRate=(30 * 60 * 1000))
    public void run() {
        repoRecordWorker.doWork();
    }
}
