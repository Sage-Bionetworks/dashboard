package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.parse.Record;
import org.springframework.stereotype.Service;

@Service("uniqueCountWriter")
public class UniqueCountWriter<R extends Record> extends AbstractMetricWriter<R, String> {

    @Override
    void write(String metricId, DateTime timestamp, String id) {
        uniqueCountDao.put(metricId, id, timestamp);
    }

    /*public void writeCertifiedUsersMetric(CuPassingRecord record, CertifiedUserMetric metric) {
        // Apply the filters first
        List<RecordFilter<CuPassingRecord>> filters = metric.getFilters();
        for (RecordFilter<CuPassingRecord> filter : filters) {
            CertifiedUserFilter cuFilter = (CertifiedUserFilter) filter;
            if (!cuFilter.matches(record)) {
                return;
            }
        }

        // Read the record
        final String metricName = metric.getName();
        final String metricId = nameIdDao.getId(metricName);
        final DateTime timestamp = record.timestamp();
        final CertifiedUserIdReader reader = (CertifiedUserIdReader) metric.getRecordReader();
        final String value = reader.read(record);

        // Write the metric
        if (value != null) {
            write(metricId, timestamp, value);
        }
    }*/

    /*public void writeResponse(CuResponseRecord record, QuestionMetric metric, boolean passed) {
        // Apply the filters first
        List<RecordFilter<CuResponseRecord>> filters = metric.getFilters();
        for (RecordFilter<CuResponseRecord> filter : filters) {
            if (passed) {
                QuestionPassFilter qpFilter = (QuestionPassFilter) filter;
                if (!qpFilter.matches(record)) {
                    return;
                }
            } else {
                QuestionFailFilter qfFilter = (QuestionFailFilter) filter;
                if (!qfFilter.matches(record)) {
                    return;
                }
            }
        }

        // Read the record
        final String metricName = metric.getName();
        final String metricId = nameIdDao.getId(metricName);
        final QuestionIndexReader reader = (QuestionIndexReader) metric.getRecordReader();
        final String questionIndex = reader.read(record);
        final String responseId = Integer.toString(record.responseId());

        // Write the metric
        if (questionIndex != null) {
            write(metricId + ":" + questionIndex, record.timestamp(), responseId);
        }
    }*/

    public void removeValue(String userId, String metricName) {
        uniqueCountDao.removeValue(userId, nameIdDao.getId(metricName));
    }

    @Resource
    private UniqueCountDao uniqueCountDao;

    @Resource
    private NameIdDao nameIdDao;
}
