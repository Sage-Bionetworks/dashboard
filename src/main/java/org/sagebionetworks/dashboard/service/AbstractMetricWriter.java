package org.sagebionetworks.dashboard.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;

abstract class AbstractMetricWriter<T> implements MetricWriter<T> {

    @Override
    public void writeMetric(final Record record, final MetricToWrite<T> metric) {
        writeMetric(record, metric, Collections.<RecordFilter> emptyList());
    }

    @Override
    public void writeMetric(final Record record, final MetricToWrite<T> metric,
            final List<RecordFilter> additionalFilters) {

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
        final RecordReader<T> reader = metric.getRecordReader();
        final T value = reader.read(record);

        // Write the metric
        if (value != null) {
            write(metricId, timestamp, value);
        }
    }

    abstract void write(String metricId, DateTime timestamp, T value);

    @Resource
    private NameIdDao nameIdDao;
}
