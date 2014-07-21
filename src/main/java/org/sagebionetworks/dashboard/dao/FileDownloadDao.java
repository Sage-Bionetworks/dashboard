package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.UserDataPoint;

/**
 * Metric for File Download Report
 */
public interface FileDownloadDao {

    void put(String metricId, String entityId, DateTime timestamp, String userData);

    List<UserDataPoint> get(String metricId, String entityId, 
            DateTime timestamp, Interval interval);
}
