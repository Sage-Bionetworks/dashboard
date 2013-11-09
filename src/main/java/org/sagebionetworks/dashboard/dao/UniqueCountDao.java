package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.sagebionetworks.dashboard.model.TimeDataPoint;

public interface UniqueCountDao {

    /**
     * Adds one to the count the specified ID.
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
     * @param n             The max number of counts to retrieve.
     */
    List<CountDataPoint> getMetric(String metricId, DateTime timestamp, long n);

    /**
     * The number of unique objects at the specified day.
     *
     * @param metricId   The ID of the metric.
     * @param timestamp  The day of the metric.
     */
    long getUniqueCount(String metricId, DateTime timestamp);

    /**
     * The number of unique objects at the specified range of days.
     *
     * @param metricId   The ID of the metric.
     * @param from       The start day of the metric.
     * @param to         The end day of the metric.
     */
    List<TimeDataPoint> getUniqueCount(String metricId, DateTime from, DateTime to);
}
