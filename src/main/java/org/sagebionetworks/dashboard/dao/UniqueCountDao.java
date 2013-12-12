package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.sagebionetworks.dashboard.model.TimeDataPoint;

public interface UniqueCountDao {

    /**
     * Puts an ID. Adds one to the count of the specified ID.
     *
     * @param metricId   The ID of the metric.
     * @param timestamp  The time when this metric was recorded.
     * @param id         The ID whose count is tracked by this metric.
     */
    void put(String metricId, DateTime timestamp, String id);

    /**
     * Gets the list of counts sorted at descending order for the specified day.
     *
     * @param metricId      The ID of the metric.
     * @param timestamp     The day of the metric.
     * @param n             The max number of counts to retrieve.
     */
    List<CountDataPoint> topCounts(String metricId, DateTime timestamp, long n);

    /**
     * The number of unique objects for the specified range of days.
     *
     * @param metricId   The ID of the metric.
     * @param from       The start day of the metric.
     * @param to         The end day of the metric.
     */
    List<TimeDataPoint> uniqueCounts(String metricId, DateTime from, DateTime to);
}
