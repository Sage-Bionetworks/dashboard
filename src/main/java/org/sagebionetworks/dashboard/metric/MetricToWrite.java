package org.sagebionetworks.dashboard.metric;

import java.util.List;

import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;

/**
 * Defines a metric written into storage.
 *
 * @param <T> Typed metric value
 */
public interface MetricToWrite<T> {

    String getName();

    List<RecordFilter> getFilters();

    RecordReader<T> getRecordReader();
}
