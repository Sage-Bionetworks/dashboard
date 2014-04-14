package org.sagebionetworks.dashboard;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.service.RepoUserWorker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Updates the list of Synapse users on a daily basis.
 */
@Component("repoUserScheduler")
public class RepoUserScheduler {

    @Resource
    private RepoUserWorker repoUserWorker;

    /**
     * Scheduled to run every 8 hour.
     */
    @Scheduled(fixedRate=(8L * 60L * 60L * 1000L))
    public void run() {
        repoUserWorker.doWork();
    }
}
