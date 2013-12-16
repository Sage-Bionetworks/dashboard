package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.TimeDataPoint;

public interface CountDao {

    /**
     * Increases the count by 1 for the specified metric at the specified time.
     */
    void put(String metricId, DateTime timestamp);

    /**
     * Gets the counts between the specified time range.
     */
    List<TimeDataPoint> get(String metricId, DateTime from, DateTime to);
}
