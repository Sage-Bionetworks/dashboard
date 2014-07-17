package org.sagebionetworks.dashboard.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractMetricWriter<T> implements MetricWriter<T> {

    private final Logger logger = LoggerFactory.getLogger(AbstractMetricWriter.class);

    @Override
    public void writeMetric(final Record record, final Metric<T> metric) {
        writeMetric(record, metric, Collections.<RecordFilter> emptyList());
    }

    @Override
    public void writeMetric(final Record record, final Metric<T> metric,
            final List<RecordFilter> additionalFilters) {

        final long start = System.nanoTime();

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

        final long readStart = System.nanoTime();
        logger.info("Filtering: " + (readStart - start));

        // Read the record
        final String metricName = metric.getName();
        final String metricId = nameIdDao.getId(metricName);
        final Long t = record.getTimestamp();
        final DateTime timestamp = new DateTime(t.longValue());
        final RecordReader<T> reader = metric.getRecordReader();
        final T value = reader.read(record);

        final long writeStart = System.nanoTime();
        logger.info("Reading: " + (writeStart - readStart));

        // Write the metric
        if (value != null) {

            write(metricId, timestamp, value);

            logger.info("Writing: " + (System.nanoTime() - writeStart));
        }
    }

    abstract void write(String metricId, DateTime timestamp, T value);

    @Resource
    private NameIdDao nameIdDao;
}
