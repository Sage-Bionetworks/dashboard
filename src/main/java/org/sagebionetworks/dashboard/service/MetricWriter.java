package org.sagebionetworks.dashboard.service;

import java.util.List;

import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.RecordFilter;

public interface MetricWriter<T> {

    void writeMetric(AccessRecord record, Metric<T> metric);

    void writeMetric(AccessRecord record, Metric<T> metric, List<RecordFilter> additionalFilters);
}
