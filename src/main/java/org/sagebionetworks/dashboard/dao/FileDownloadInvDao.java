package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.UserDataPoint;

/**
 * Metric for File Download Report
 */
public interface FileDownloadInvDao {

    void put(String metricId, String entityId, DateTime timestamp, 
            UserDataPoint userData);

    List<UserDataPoint> get(String metricId, String entityId, 
            DateTime timestamp, Interval interval);
}
