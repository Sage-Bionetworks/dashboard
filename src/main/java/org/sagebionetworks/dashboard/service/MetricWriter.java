package org.sagebionetworks.dashboard.service;

import java.util.List;

import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordFilter;

public interface MetricWriter<R extends Record, V> {

    void writeMetric(R record, Metric<R, V> metric);

    void writeMetric(R record, Metric<R, V> metric, List<RecordFilter<R>> additionalFilters);
}
