package org.sagebionetworks.dashboard.dao.redis;

import java.util.Set;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.KeyCachedDao;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.redis.Key;
import org.sagebionetworks.dashboard.parse.CuResponseRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Repository("keyCachedDao")
public class KeyCachedDaoImpl implements KeyCachedDao {

    @Resource(name="redisTemplate")
    private ZSetOperations<String, String> zsetOps;

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource
    private NameIdDao nameIdDao;

    private static final String UNIQUECOUNT_PREFIX = "n:month:uniquecount:";

    @Override
    public Set<String> getAllKeys(String metricName, String id) {
        return redisTemplate.keys(getKey(metricName, id));
    }

    @Override
    public void put(CuResponseRecord record, String metric) {
        String key = getKey(metric, Integer.toString(record.questionIndex()));
        String keyMember = getKeyMember(metric, record);
        zsetOps.incrementScore(key, keyMember, 1.0d);
    }

    private String getKeyMember(String metric, CuResponseRecord record) {
        return UNIQUECOUNT_PREFIX + getKeyMember(metric, record) + Key.SEPARATOR
                + Long.toString(record.getTimestamp());
    }

    private String getKey(String metricName, String id) {
        String metricId = nameIdDao.getId(metricName);
        return metricId + Key.SEPARATOR + id;
    }
}
