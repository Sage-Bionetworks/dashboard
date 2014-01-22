package org.sagebionetworks.dashboard;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.service.RepoRecordWorker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("repoRecordScheduler")
public class RepoRecordScheduler {

    @Resource
    private RepoRecordWorker repoRecordWorker;

    /**
     * Updates every 60 minutes.
     */
    @Scheduled(fixedRate=(60 * 60 * 1000))
    public void run() {
        repoRecordWorker.doWork();
    }
}
