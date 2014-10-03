package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.KeyCachedDao;
import org.sagebionetworks.dashboard.metric.CertifiedUserMetric;
import org.sagebionetworks.dashboard.metric.CertifiedUserQuizSubmitMetric;
import org.sagebionetworks.dashboard.metric.QuestionFailMetric;
import org.sagebionetworks.dashboard.metric.QuestionPassMetric;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.CuPassingRecord;
import org.sagebionetworks.dashboard.parse.CuResponseRecord;
import org.springframework.stereotype.Service;

@Service("updateCuPassingRecordService")
public class UpdateCuPassingRecordService {

    @Resource
    private CertifiedUserMetric certifiedUsersMetric;

    @Resource
    private UniqueCountWriter<CuPassingRecord> uniqueCountWriterForPassingRecord;

    @Resource
    private UniqueCountWriter<CuResponseRecord> uniqueCountWriterForResponseRecord;

    @Resource
    private UniqueCountWriter<AccessRecord> uniqueCountWriterForAccessRecord;

    @Resource
    private QuestionPassMetric questionPassMetric;

    @Resource
    private QuestionFailMetric questionFailMetric;

    @Resource
    private CertifiedUserQuizSubmitMetric submissionMetric;

    @Resource
    private KeyCachedDao keyDao;

    /**
     * process a given passing record and update certified users metric
     */
    public void updateCertifiedUsers(CuPassingRecord record) {
        if (record != null && record.isPassed()) {
            uniqueCountWriterForPassingRecord.writeMetric(record, certifiedUsersMetric);
        }
    }

    /**
     * process a given response record and update question pass/fail metric
     */
    public void updateResponses(CuResponseRecord record) {
        if (record == null) {
            return;
        }
        uniqueCountWriterForResponseRecord.writeMetric(record,
                questionPassMetric, ":" + Integer.toString(record.questionIndex()));
        uniqueCountWriterForResponseRecord.writeMetric(record,
                questionFailMetric, ":" + Integer.toString(record.questionIndex()));
        // cache the key
        keyDao.put(record, questionPassMetric.getName());
    }

    /**
     * remove the user whom takes the test in staging, but not in production
     */
    public void removeSubmission(String userId) {
        uniqueCountWriterForAccessRecord.removeValue(userId, submissionMetric.getName());
    }
}
