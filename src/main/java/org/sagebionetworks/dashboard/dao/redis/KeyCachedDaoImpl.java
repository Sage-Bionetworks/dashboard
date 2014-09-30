package org.sagebionetworks.dashboard.dao.redis;

import java.util.List;

import org.sagebionetworks.dashboard.dao.KeyCachedDao;
import org.sagebionetworks.dashboard.parse.CuResponseRecord;
import org.springframework.stereotype.Repository;

@Repository("keyCachedDao")
public class KeyCachedDaoImpl implements KeyCachedDao {

    @Override
    public List<String> getAllKeys(String metricName, String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void put(CuResponseRecord record, String metric) {
        // TODO Auto-generated method stub
        
    }

}
