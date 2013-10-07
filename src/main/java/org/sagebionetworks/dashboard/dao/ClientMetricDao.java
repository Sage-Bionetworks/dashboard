package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.DataPoint;
import org.sagebionetworks.dashboard.model.redis.Aggregation;
import org.sagebionetworks.dashboard.model.redis.Statistic;

/**
 * Metrics about the various clients using Synapse.
 */
public interface ClientMetricDao {

    void addMetric(String clientId, DateTime timestamp, long latency);

    List<DataPoint> getMetrics(String clientId, DateTime from, DateTime to,
            Statistic statistic, Aggregation aggregation);
}
