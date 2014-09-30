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
    /* use a special character that is not a special character in regular expression 
       to end the key so that getAllKeys wont return the keys that does not match completely. */
    private static final String END_OF_KEY = "#";

    @Override
    public Set<String> getAllKeys(String metricName, String id) {
        return redisTemplate.keys(getKey(metricName, id) + END_OF_KEY);
    }

    @Override
    public void put(CuResponseRecord record, String metric) {
        String key = getKey(metric, Integer.toString(record.questionIndex()));
        String keyMember = getKeyMember(metric, record);
        zsetOps.incrementScore(key + END_OF_KEY, keyMember, 1.0d);
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
