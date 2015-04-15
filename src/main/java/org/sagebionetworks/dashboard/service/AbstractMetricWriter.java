package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.model.Record;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;

abstract class AbstractMetricWriter<R extends Record, V> implements MetricWriter<R, V> {

    @Override
    public void writeMetric(final R record, final Metric<R, V> metric) {
        writeMetric(record, metric, "");
    }

    @Override
    public void writeMetric(final R record, final Metric<R, V> metric, final String additionalKey) {

        // Apply the filters first
        List<RecordFilter<R>> filters = metric.getFilters();
        for (RecordFilter<R> filter : filters) {
            if (!filter.matches(record)) {
                return;
            }
        }

        // Read the record
        final String metricName = metric.getName();
        final String metricId = nameIdDao.getId(metricName);
        final Long t = record.getTimestamp();
        final DateTime timestamp = new DateTime(t.longValue());
        final RecordReader<R, V> reader = metric.getRecordReader();
        final V value = reader.read(record);

        // Write the metric
        if (value != null) {
            write(metricId, additionalKey, timestamp, value);
        }
    }

    @Resource
    private NameIdDao nameIdDao;

    abstract void write(String metricId, String additionalKey, DateTime timestamp, V value);
}
