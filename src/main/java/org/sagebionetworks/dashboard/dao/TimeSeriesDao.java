package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.sagebionetworks.dashboard.model.redis.Aggregation;
import org.sagebionetworks.dashboard.model.redis.Statistic;

/**
 * Metrics of time series data.
 */
public interface TimeSeriesDao {

    /**
     * Adds a metric of a time series data point.
     *
     * @param metricId   The ID of the metric.
     * @param timestamp  The time when this metric was recorded.
     * @param value      Time series data value.
     */
    void addMetric(String metricId, DateTime timestamp, long value);

    /**
     * Gets the metric as a time series.
     *
     * @param metricId      The ID of the client metric.
     * @param from          Time range of the metric. This is the starting point.
     * @param to            Time range of the metric. This is the ending point.
     * @param statistic     Statistic n, sum, average, or max.
     * @param aggregation   Aggregation by minute, hour, or day.
     */
    List<TimeDataPoint> getMetric(String metricId, DateTime from, DateTime to,
            Statistic statistic, Aggregation aggregation);
}
