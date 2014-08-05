package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.RedisConstants.EXPIRE_DAYS;
import static org.sagebionetworks.dashboard.model.Interval.day;
import static org.sagebionetworks.dashboard.model.Interval.month;
import static org.sagebionetworks.dashboard.model.Interval.week;

import java.util.Date;
import java.util.Set;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.model.Interval;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Repository("uniqueCountDao")
public class UniqueCountDaoImpl extends AbstractUniqueCountDao {

    @Override
    public void put(String metricId, String id, DateTime timestamp) {
        String shortId = nameIdDao.getId(id);
        put(metricId, shortId, day, timestamp);
        put(metricId, shortId, week, timestamp);
        put(metricId, shortId, month, timestamp);
    }

    public Set<String> getAllKeys(String metricId) {
        String pattern = "*" + metricId + "*";
        return redisTemplate.keys(pattern);
    }

    private void put(String metricId, String shortId, Interval interval, DateTime timestamp) {
        String key = getKey(metricId, interval, timestamp);
        zsetOps.incrementScore(key, shortId, 1.0d);
        Date expireAt = DateTime.now().plusDays(EXPIRE_DAYS).toDate();
        redisTemplate.expireAt(key, expireAt);
    }

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource(name="redisTemplate")
    private ZSetOperations<String, String> zsetOps;

    @Resource
    private NameIdDao nameIdDao;
}
