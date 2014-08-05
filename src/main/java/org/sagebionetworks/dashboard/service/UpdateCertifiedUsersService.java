package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.metric.CertifiedUsersMetric;
import org.sagebionetworks.dashboard.parse.CuPassingRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("updateCertifiedUsersService")
public class UpdateCertifiedUsersService {

    private final Logger logger = LoggerFactory.getLogger(UpdateCertifiedUsersService.class);

    @Resource
    private CertifiedUsersMetric certifiedUsersMetric;

    @Resource
    private UniqueCountWriter uniqueCountWriter;

    /**
     * process a given passing record and update 3 metrics:
     *  certified users
     *  pass questions
     *  fail questions
     */
    public void updateCertifiedUsers(CuPassingRecord record) {
        if (record != null && record.isPassed()) {
            uniqueCountWriter.writeCertifiedUsersMetric(record, certifiedUsersMetric);
        }
    }
}
