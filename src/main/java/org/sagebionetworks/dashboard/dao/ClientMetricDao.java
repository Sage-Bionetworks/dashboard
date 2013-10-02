package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.sagebionetworks.dashboard.model.DataPoint;

/**
 * Metrics about the various clients using Synapse.
 */
public interface ClientMetricDao {

    void addMetric(String clientId, long latency, long posixTime);

    List<DataPoint> getMetrics(String clientId, long posixStart, long posixEnd, String metric, String aggregation);
}
