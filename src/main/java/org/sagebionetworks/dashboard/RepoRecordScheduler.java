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
     * Initial delay of 7 minutes. Updates every 23 minutes.
     */
    @Scheduled(initialDelay=(7L * 60L * 1000L), fixedRate=(23L * 60L * 1000L))
    public void runRecordWorker() {
        repoRecordWorker.doWork();
    }

    /**
     * Initial delay of 37 minutes. Runs every 17 hours.
     */
   // @Scheduled(initialDelay=(33L * 60L * 1000L), fixedRate=(17L * 60L * 60L * 1000L))
    public void runRepairWorker() {
        repoRepairWorker.doWork();
    }
}
