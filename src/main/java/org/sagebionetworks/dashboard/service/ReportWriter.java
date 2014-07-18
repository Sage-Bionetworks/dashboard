package org.sagebionetworks.dashboard.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.FileDownloadInvDao;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;

public class ReportWriter implements MetricWriter<String>{

    @Override
    public void writeMetric(final Record record, final Metric<String> metric) {
        writeMetric(record, metric, Collections.<RecordFilter> emptyList());
    }

    @Override
    public void writeMetric(Record record, Metric<String> metric,
            List<RecordFilter> additionalFilters) {
     // Apply the filters first
        List<RecordFilter> filters = metric.getFilters();
        for (RecordFilter filter : filters) {
            if (!filter.matches(record)) {
                return;
            }
        }
        for (RecordFilter filter : additionalFilters) {
            if (!filter.matches(record)) {
                return;
            }
        }

        // Read the record
        final String metricName = metric.getName();
        final String metricId = nameIdDao.getId(metricName);
        final Long t = record.getTimestamp();
        final DateTime timestamp = new DateTime(t.longValue());
        final RecordReader<String> reader = metric.getRecordReader();
        final String value = reader.read(record);
        final String entityId = getEntityId(record);

        // Write the metric
        if (value != null) {
            write(metricId, timestamp, entityId, value);
        }
    }

    private String getEntityId(Record record) {
        // TODO Auto-generated method stub
        return null;
    }

    void write(String metricId, DateTime timestamp, String entityId, String userData) {
        fileDownloadInvDao.put(metricId, entityId, timestamp, userData);
    }

    @Resource
    private FileDownloadInvDao fileDownloadInvDao;

    @Resource
    private NameIdDao nameIdDao;
}
