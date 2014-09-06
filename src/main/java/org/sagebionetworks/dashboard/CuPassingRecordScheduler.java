package org.sagebionetworks.dashboard;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.service.CuPassingRecordWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("cuPassingRecordScheduler")
public class CuPassingRecordScheduler {

    private final Logger logger = LoggerFactory.getLogger(CuPassingRecordScheduler.class);

    @Resource
    private CuPassingRecordWorker worker;

    @Scheduled(initialDelay=(13L * 60L * 1000L), fixedRate=(23L * 60L * 1000L))
    public void run() {
        logger.info("CuPassingRecordScheduler.run() thread ID: " + Thread.currentThread().getId());
        worker.doWork();
    }
}
