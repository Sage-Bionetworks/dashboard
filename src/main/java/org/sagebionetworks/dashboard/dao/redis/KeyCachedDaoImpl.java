package org.sagebionetworks.dashboard.dao.redis;

import java.util.Set;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.KeyCachedDao;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.redis.Key;
import org.sagebionetworks.dashboard.parse.CuResponseRecord;
import org.sagebionetworks.dashboard.util.PosixTimeUtil;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Repository("keyCachedDao")
public class KeyCachedDaoImpl implements KeyCachedDao {

    @Resource(name="redisTemplate")
    private ZSetOperations<String, String> zsetOps;

    @Resource
    private NameIdDao nameIdDao;

    private static final String UNIQUECOUNT_PREFIX = "n:month:uniquecount:";
    /* use a special character that is not a special character in regular expression 
       to end the key so that getAllKeys wont return the keys that does not match completely. */
    private static final String END_OF_KEY = "#";

    @Override
    public Set<String> getAllKeys(String metricName, String id) {
        return zsetOps.range(getKey(metricName, id) + END_OF_KEY, 0, Integer.MAX_VALUE);
    }

    @Override
    public void put(CuResponseRecord record, String metric) {
        String key = getKey(metric, Integer.toString(record.questionIndex()));
        String keyMember = getKeyMember(metric, record);
        zsetOps.incrementScore(key + END_OF_KEY, keyMember, 1.0d);
    }

    private String getKeyMember(String metric, CuResponseRecord record) {
        final Long t = record.getTimestamp();
        final DateTime timestamp = new DateTime(t.longValue());
        System.out.println(t);
        System.out.println(timestamp.getMillis());
        System.out.println(PosixTimeUtil.floorToMonth(timestamp));
        return UNIQUECOUNT_PREFIX + getKey(metric, Integer.toString(record.questionIndex())) 
                + Key.SEPARATOR + Long.toString(PosixTimeUtil.floorToMonth(timestamp));
    }

    private String getKey(String metricName, String id) {
        String metricId = nameIdDao.getId(metricName);
        return metricId + Key.SEPARATOR + id;
    }
}
