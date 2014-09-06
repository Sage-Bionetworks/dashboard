package org.sagebionetworks.dashboard;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.service.CuPassingRecordWorker;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("cuPassingRecordScheduler")
public class CuPassingRecordScheduler {

    @Resource
    private CuPassingRecordWorker worker;

    @Async
    @Scheduled(initialDelay=(13L * 60L * 1000L), fixedRate=(23L * 60L * 1000L))
    public void run() {
        worker.doWork();
    }
}
