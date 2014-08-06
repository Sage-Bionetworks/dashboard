package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.metric.CertifiedUsersMetric;
import org.sagebionetworks.dashboard.metric.QuestionMetric;
import org.sagebionetworks.dashboard.parse.CertifiedUserFilter;
import org.sagebionetworks.dashboard.parse.CertifiedUserIdReader;
import org.sagebionetworks.dashboard.parse.CuPassingRecord;
import org.sagebionetworks.dashboard.parse.QuestionFailFilter;
import org.sagebionetworks.dashboard.parse.QuestionIndexReader;
import org.sagebionetworks.dashboard.parse.QuestionPassFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.Response;
import org.springframework.stereotype.Service;

@Service("uniqueCountWriter")
public class UniqueCountWriter extends AbstractMetricWriter<String> {

    @Override
    void write(String metricId, DateTime timestamp, String id) {
        uniqueCountDao.put(metricId, id, timestamp);
    }

    @Resource
    private UniqueCountDao uniqueCountDao;

    public void writeCertifiedUsersMetric(CuPassingRecord record, CertifiedUsersMetric metric) {
        // Apply the filters first
        List<RecordFilter> filters = metric.getFilters();
        for (RecordFilter filter : filters) {
            if (filter.getClass().isInstance(CertifiedUserFilter.class)) {
                CertifiedUserFilter cuFilter = (CertifiedUserFilter) filter;
                if (!cuFilter.matches(record)) {
                    return;
                }
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
    }

    public void writeResponse(Response record,
            QuestionMetric metric, DateTime timestamp) {
     // Apply the filters first
        List<RecordFilter> filters = metric.getFilters();
        for (RecordFilter filter : filters) {
            if (filter.getClass().isInstance(QuestionPassFilter.class)) {
                QuestionPassFilter qpFilter = (QuestionPassFilter) filter;
                if (!qpFilter.matches(record)) {
                    return;
                }
            } else if (filter.getClass().isInstance(QuestionFailFilter.class)) {
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
        final String value = reader.read(record);

        // Write the metric
        if (value != null) {
            write(metricId, timestamp, value);
        }
    }
}
