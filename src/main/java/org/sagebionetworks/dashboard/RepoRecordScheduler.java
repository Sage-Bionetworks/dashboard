package org.sagebionetworks.dashboard;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.service.RepoRecordWorker;
import org.sagebionetworks.dashboard.service.RepoRepairWorker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Updates the audit records periodically.
 */
@Component("repoRecordScheduler")
public class RepoRecordScheduler {

    @Resource
    private RepoRecordWorker repoRecordWorker;

    @Resource
    private RepoRepairWorker repoRepairWorker;

    /**
     * Initial delay of 5 minutes. Updates every 29 minutes.
     */
    @Scheduled(initialDelay=(5L * 60L * 1000L), fixedRate=(29L * 60L * 1000L))
    public void runRecordWorker() {
        repoRecordWorker.doWork();
    }

    /**
     * Initial delay of 33 minutes. Runs every 11 hours.
     */
    @Scheduled(initialDelay=(33L * 60L * 1000L), fixedRate=(11L * 60L * 60L * 1000L))
    public void runRepairWorker() {
        repoRepairWorker.doWork();
    }
}
