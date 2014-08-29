package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.NameSpace.bitcount;
import static org.sagebionetworks.dashboard.dao.redis.RedisConstants.EXPIRE_DAYS;
import static org.sagebionetworks.dashboard.model.Interval.month;
import static org.sagebionetworks.dashboard.model.Interval.week;
import static org.sagebionetworks.dashboard.model.Statistic.n;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.model.Interval;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

/**
 * Counts the number of unique days for the given ID.
 */
@Repository("dayCountDao")
public class DayCountDaoImpl extends AbstractUniqueCountDao {

    @Override
    public void put(String metricId, String id, DateTime timestamp) {
        String shortId = nameIdDao.getId(id);
        put(metricId, shortId, week, timestamp);
        put(metricId, shortId, month, timestamp);
    }

    private void put(String metricId, String shortId, Interval interval, DateTime timestamp) {
        final String bitCountKey = getBitCountKey(metricId, shortId, interval, timestamp);
        final int day = getDay(interval, timestamp);
        if (!isDaySet(bitCountKey, day)) {
            setDay(bitCountKey, day);
            final String key = getKey(metricId, interval, timestamp);
            long dayCount = getDayCount(bitCountKey);
            zsetOps.add(key, shortId, dayCount);
            redisTemplate.expireAt(key, DateTime.now().plusDays(EXPIRE_DAYS).toDate());
        }
    }

    private String getBitCountKey(String metricId, String shortId, Interval interval, DateTime timestamp) {
        String id = metricId + Key.SEPARATOR + shortId;
        switch (interval) {
            case week:
                return KEY_ASSEMBLER_WEEK.getKey(id, timestamp);
            case month:
                return KEY_ASSEMBLER_MONTH.getKey(id, timestamp);
            default:
                throw new IllegalArgumentException("Interval " + interval + " is not supported.");
        }
    }

    private int getDay(Interval interval, DateTime timestamp) {
        switch (interval) {
            case week:
                return timestamp.getDayOfWeek();
            case month:
                return timestamp.getDayOfMonth();
            default:
                throw new IllegalArgumentException("Interval " + interval + " is not supported.");
        }
    }

    private boolean isDaySet(final String key, final int day) {
        return redisTemplate.execute(new RedisCallback<Boolean> () {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection conn = (StringRedisConnection)connection;
                return conn.getBit(key, day);
            }
        });
    }

    private Long getDayCount(final String key) {
        return redisTemplate.execute(new RedisCallback<Long> () {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection conn = (StringRedisConnection)connection;
                return conn.bitCount(key);
            }
        });
    }

    private void setDay(final String key, final int day) {
        redisTemplate.execute(new RedisCallback<Object> () {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection conn = (StringRedisConnection)connection;
                conn.setBit(key, day, true);
                return null;
            }
        });
        // Cache the counter for at most 1 month
        DateTime now = DateTime.now();
        int expireDays = now.dayOfMonth().getMaximumValue() + 1;
        redisTemplate.expireAt(key, now.plusDays(expireDays).toDate());
    }

    private static final KeyAssembler KEY_ASSEMBLER_WEEK = new KeyAssembler(n, week, bitcount);
    private static final KeyAssembler KEY_ASSEMBLER_MONTH = new KeyAssembler(n, month, bitcount);

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource(name="redisTemplate")
    private ZSetOperations<String, String> zsetOps;

    @Resource
    private NameIdDao nameIdDao;

}
