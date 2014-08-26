package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.metric.CertifiedUserMetric;
import org.sagebionetworks.dashboard.metric.QuestionFailMetric;
import org.sagebionetworks.dashboard.metric.QuestionPassMetric;
import org.sagebionetworks.dashboard.parse.CuPassingRecord;
import org.sagebionetworks.dashboard.parse.Response;
import org.springframework.stereotype.Service;

@Service("updateCuPassingRecordService")
public class UpdateCuPassingRecordService {

    @Resource
    private CertifiedUserMetric certifiedUsersMetric;

    @Resource
    private UniqueCountWriter uniqueCountWriter;

    @Resource
    private QuestionPassMetric questionPassMetric;

    @Resource
    private QuestionFailMetric questionFailMetric;

    /**
     * process a given passing record and update certified users metric
     */
    public void updateCertifiedUsers(CuPassingRecord record) {
        if (record != null && record.isPassed()) {
            uniqueCountWriter.writeCertifiedUsersMetric(record, certifiedUsersMetric);
        }
    }

    /**
     * process a given passing record and update certified users metric
     */
    public void updateResponses(Response record, DateTime timestamp) {
        if (record != null && record.isCorrect()) {
            uniqueCountWriter.writeResponse(record, questionPassMetric, timestamp, true);
        } else if (record != null) {
            uniqueCountWriter.writeResponse(record, questionFailMetric, timestamp, false);
        }
    }
}
