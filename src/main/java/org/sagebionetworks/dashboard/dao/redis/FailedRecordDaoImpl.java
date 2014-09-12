package org.sagebionetworks.dashboard.dao.redis;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.FailedRecordDao;
import org.sagebionetworks.dashboard.model.WriteRecordResult;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

@Repository("failedRecordDao")
public class FailedRecordDaoImpl implements FailedRecordDao{

    @Override
    public void put(WriteRecordResult result) {
        setOps.add(key, result.toString());
    }

    @Resource(name="redisTemplate")
    private SetOperations<String, String> setOps;

    private static final String key = "failedRecord";
}
