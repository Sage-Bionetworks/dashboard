package org.sagebionetworks.dashboard.dao.redis;

import java.util.Set;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.KeyCachedDao;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.redis.Key;
import org.sagebionetworks.dashboard.parse.CuResponseRecord;
import org.sagebionetworks.dashboard.util.PosixTimeUtil;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

@Repository("keyCachedDao")
public class KeyCachedDaoImpl implements KeyCachedDao {

    @Resource(name="redisTemplate")
    private SetOperations<String, String> zsetOps;

    @Resource
    private NameIdDao nameIdDao;

    private static final String UNIQUECOUNT_PREFIX = "n:month:uniquecount:";

    @Override
    public Set<String> getAllKeys(String metricName, String id) {
        return zsetOps.members(getKey(metricName, id));
    }

    @Override
    public void put(CuResponseRecord record, String metric) {
        String key = getKey(metric, Integer.toString(record.questionIndex()));
        String keyMember = getKeyMember(metric, record);
        zsetOps.add(key, keyMember);
    }

    private String getKeyMember(String metric, CuResponseRecord record) {
        final Long t = record.getTimestamp();
        final DateTime timestamp = new DateTime(t.longValue());
        return UNIQUECOUNT_PREFIX + getKey(metric, Integer.toString(record.questionIndex())) 
                + Key.SEPARATOR + Long.toString(PosixTimeUtil.floorToMonth(timestamp));
    }

    private String getKey(String metricName, String id) {
        String metricId = nameIdDao.getId(metricName);
        return metricId + Key.SEPARATOR + id;
    }
}
