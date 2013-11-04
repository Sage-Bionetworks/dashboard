package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.sagebionetworks.dashboard.model.redis.Aggregation;
import org.sagebionetworks.dashboard.model.redis.Statistic;

/**
 * Metrics about the various clients using Synapse.
 */
public interface ClientMetricDao {

    /**
     * Adds a metric of latency.
     *
     * @param clientId   The ID of the client metric.
     * @param timestamp  The time when this metric was recorded.
     * @param latency    Latency in milliseconds.
     */
    void addLatencyMetric(String metricId, DateTime timestamp, long latency);

    /**
     * Gets the metric as a time series.
     *
     * @param metricId      The ID of the client metric.
     * @param from          Time range of the metric. This is the starting point.
     * @param to            Time range of the metric. This is the ending point.
     * @param statistic     Statistic n, sum, average, or max.
     * @param aggregation   Aggregation by minute, hour, or day.
     * @return
     */
    List<TimeDataPoint> getMetric(String metricId, DateTime from, DateTime to,
            Statistic statistic, Aggregation aggregation);
}
