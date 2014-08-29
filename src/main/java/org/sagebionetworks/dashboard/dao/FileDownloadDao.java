package org.sagebionetworks.dashboard.dao;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.UserDataPoint;

public interface FileDownloadDao {

    void put(String metricId, String entityId, DateTime timestamp, String userData);

    List<UserDataPoint> get(String metricId, String entityId, 
            DateTime timestamp, Interval interval);

    List<UserDataPoint> get(String key);

    Set<String> getAllKeys(String metricId);
}
