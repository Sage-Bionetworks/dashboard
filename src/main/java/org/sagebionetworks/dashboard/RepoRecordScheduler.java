package org.sagebionetworks.dashboard;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.service.RepoRecordWorker;
import org.sagebionetworks.dashboard.service.RepoRepairWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Updates the audit records periodically.
 */
@Component("repoRecordScheduler")
public class RepoRecordScheduler {

    private final Logger logger = LoggerFactory.getLogger(RepoRecordScheduler.class);

    @Resource
    private RepoRecordWorker repoRecordWorker;

    @Resource
    private RepoRepairWorker repoRepairWorker;

    /**
     * Initial delay of 7 minutes. Updates every 23 minutes.
     */
    @Async
    @Scheduled(initialDelay=(7L * 60L * 1000L), fixedRate=(23L * 60L * 1000L))
    public void runRecordWorker() {
        logger.info("RepoRecordScheduler.runRecordWorker() thread ID: " + Thread.currentThread().getId());
        repoRecordWorker.doWork();
    }

    /**
     * Initial delay of 37 minutes. Runs every 17 hours.
     */
    @Async
    @Scheduled(initialDelay=(33L * 60L * 1000L), fixedRate=(17L * 60L * 60L * 1000L))
    public void runRepairWorker() {
        logger.info("RepoRecordScheduler.runRepairWorker() thread ID: " + Thread.currentThread().getId());
        repoRepairWorker.doWork();
    }
}
