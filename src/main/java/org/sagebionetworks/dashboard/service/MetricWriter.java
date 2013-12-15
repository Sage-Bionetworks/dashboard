package org.sagebionetworks.dashboard.service;

import java.util.List;

import org.sagebionetworks.dashboard.metric.MetricToWrite;
import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordFilter;

public interface MetricWriter<T> {

    void writeMetric(Record record, MetricToWrite<T> metric);

    void writeMetric(Record record, MetricToWrite<T> metric, List<RecordFilter> additionalFilters);
}
