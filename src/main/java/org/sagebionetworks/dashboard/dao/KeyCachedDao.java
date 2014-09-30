package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.sagebionetworks.dashboard.parse.CuResponseRecord;

public interface KeyCachedDao {

    /**
     * @return all keys with metricName and id
     */
    List<String> getAllKeys(String metricName, String id);

    /**
     * Metric store the record's data as a member of a key in redis
     * To save time looking up all keys from the same metric, add this key as
     * a member of another key. 
     */
    void put(CuResponseRecord record, String metric);
}
