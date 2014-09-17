package org.sagebionetworks.dashboard.service;

import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.parse.Record;

public interface MetricWriter<R extends Record, V> {

    void writeMetric(R record, Metric<R, V> metric);

    void writeMetric(R record, Metric<R, V> metric, String additionalKey);

}
