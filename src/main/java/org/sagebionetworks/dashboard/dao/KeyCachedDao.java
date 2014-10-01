package org.sagebionetworks.dashboard.dao;

import java.util.Set;

import org.sagebionetworks.dashboard.parse.CuResponseRecord;

public interface KeyCachedDao {

    /**
     * @return all keys with metricName and id
     */
    Set<String> getAllKeys(String metricName, String id);

    /**
     * Metric store the record's data as a member of a key in redis
     * To save time looking up all keys from the same metric, add this key as
     * a member of another key. 
     */
    void put(CuResponseRecord record, String metric);
}
