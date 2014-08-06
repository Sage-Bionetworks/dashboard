package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.metric.CertifiedUserMetric;
import org.sagebionetworks.dashboard.metric.QuestionFailMetric;
import org.sagebionetworks.dashboard.metric.QuestionPassMetric;
import org.sagebionetworks.dashboard.parse.CuPassingRecord;
import org.sagebionetworks.dashboard.parse.Response;
import org.springframework.stereotype.Service;

@Service("updateCertifiedUsersService")
public class UpdateCertifiedUserService {

    @Resource
    private CertifiedUserMetric certifiedUsersMetric;

    @Resource
    private UniqueCountWriter uniqueCountWriter;

    @Resource
    private QuestionPassMetric questionPassMetric;

    @Resource
    private QuestionFailMetric questionFailMetric;

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
        if (record != null && record.responses() != null) {
            for (Response res : record.responses()) {
                if (res.isCorrect()) {
                    uniqueCountWriter.writeResponse(res, questionPassMetric, record.timestamp());
                } else {
                    uniqueCountWriter.writeResponse(res, questionFailMetric, record.timestamp());
                }
            }
        }
    }
}
