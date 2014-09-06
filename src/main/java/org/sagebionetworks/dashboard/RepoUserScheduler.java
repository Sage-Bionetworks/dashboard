package org.sagebionetworks.dashboard;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.service.RepoUserWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Updates the list of Synapse users on a daily basis.
 */
@Component("repoUserScheduler")
public class RepoUserScheduler {

    private final Logger logger = LoggerFactory.getLogger(RepoRecordScheduler.class);

    @Resource
    private RepoUserWorker repoUserWorker;

    /**
     * Scheduled to run every 8 hour.
     */
    @Scheduled(initialDelay=1L, fixedRate=(8L * 60L * 60L * 1000L))
    public void run() {
        logger.info("RepoUserScheduler.run() thread ID: " + Thread.currentThread().getId());
        repoUserWorker.doWork();
    }
}
