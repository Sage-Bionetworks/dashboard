package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.CountDataPoint;

public interface UniqueCountDao {

    /**
     * Adds one count to the specified ID.
     *
     * @param metricId   The ID of the metric.
     * @param timestamp  The time when this metric was recorded.
     * @param id         The ID whose count is tracked by this metric.
     */
    void addMetric(String metricId, DateTime timestamp, String id);

    /**
     * Gets the list of counts sorted at descending order at the specified day.
     *
     * @param metricId      The ID of the metric.
     * @param timestamp     The day of the metric.
     */
    List<CountDataPoint> getMetric(String metricId, DateTime timestamp);

    /**
     * The number of unique objects at the specified day.
     *
     * @param metricId   The ID of the metric.
     * @param timestamp  The day of the metric.
     */
    long getUniqueCount(String metricId, DateTime timestamp);
}
