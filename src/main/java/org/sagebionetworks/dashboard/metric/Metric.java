package org.sagebionetworks.dashboard.metric;

import java.util.List;

import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;

/**
 * Defines a metric written into storage.
 *
 * @param <V> Typed metric value
 */
public interface Metric<R extends Record, V> {

    String getName();

    List<RecordFilter<R>> getFilters();

    RecordReader<R, V> getRecordReader();
}
