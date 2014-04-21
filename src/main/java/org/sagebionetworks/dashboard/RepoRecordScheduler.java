package org.sagebionetworks.dashboard;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.service.RepoRecordWorker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Updates the audit records periodically.
 */
@Component("repoRecordScheduler")
public class RepoRecordScheduler {

    @Resource
    private RepoRecordWorker repoRecordWorker;

    /**
     * Initial delay of 5 minutes. Updates every 25 minutes.
     */
    @Scheduled(initialDelay=(5L * 60L * 1000L), fixedRate=(25L * 60L * 1000L))
    public void run() {
        repoRecordWorker.doWork();
    }
}
