package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.TimeDataPoint;

/**
 * Counts a set of unique IDs. Each ID in the set has an associated count.
 */
public interface UniqueCountDao {

    /**
     * Increased the count of the specified ID by one. If the ID is not in
     * the set yet, it will be added with a count of one.
     *
     * @param metricId   The ID of the metric.
     * @param id         The object ID whose count is to be increased.
     * @param timestamp  The time-stamp when this metric was captured.
     */
    void put(String metricId, String id, DateTime timestamp);

    /**
     * Gets the list of counts for a particular ID over a period of time. For example,
     * the list of session counts for a user over a period of time.
     *
     * @param metricId   The ID of the metric.
     * @param id         The object ID.
     * @param interval   Aggregation interval. By day, by week, or by month.
     * @param from       The start time of the metric.
     * @param to         The end time of the metric.
     */
    List<TimeDataPoint> get(String metricId, String id, Interval interval, DateTime from, DateTime to);

    /**
     * Gets the list of IDs for the specified time with their counts sorted at descending order.
     *
     * @param metricId   The ID of the metric.
     * @param interval   Aggregation interval. By day, by week, or by month.
     * @param timestamp  The time of the metric.
     * @param offset     Zero-based offset.
     * @param size       The max number of counts to retrieve.
     */
    List<CountDataPoint> getTop(String metricId, Interval interval, DateTime timestamp, long offset, long size);

    /**
     * The number of unique objects for the specified time.
     *
     * @param metricId   The ID of the metric.
     * @param interval   Aggregation interval. By day, by week, or by month.
     * @param from       The start time of the metric.
     * @param to         The start time of the metric.
     */
    List<TimeDataPoint> getUnique(String metricId, Interval interval, DateTime from, DateTime to);
}
