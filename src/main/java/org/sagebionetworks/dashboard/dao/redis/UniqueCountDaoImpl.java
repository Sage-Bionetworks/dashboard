package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.NameSpace.uniquecount;
import static org.sagebionetworks.dashboard.dao.redis.RedisConstants.EXPIRE_DAYS;
import static org.sagebionetworks.dashboard.model.Interval.day;
import static org.sagebionetworks.dashboard.model.Interval.month;
import static org.sagebionetworks.dashboard.model.Interval.week;
import static org.sagebionetworks.dashboard.model.Statistic.n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.sagebionetworks.dashboard.util.PosixTimeUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

@Repository("uniqueCountDao")
public class UniqueCountDaoImpl implements UniqueCountDao {

    @Override
    public void put(String metricId, String id, DateTime timestamp) {
        String shortId = nameIdDao.getId(id); // Swap for a shorter id
        put(metricId, shortId, day, timestamp);
        put(metricId, shortId, week, timestamp);
        put(metricId, shortId, month, timestamp);
    }

    @Override
    public List<TimeDataPoint> counts(String metricId, String id, Interval interval, DateTime from, DateTime to) {
        final String shortId = nameIdDao.getId(id); // Swap for a shorter id
        final List<String> keys = getKeys(metricId, interval, from, to);
        final List<Boolean> exists = keyExists(keys);
        List<Object> counts = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection conn) throws DataAccessException {
                StringRedisConnection redisConn = (StringRedisConnection)conn;
                for (int i = 0; i < keys.size(); i++) {
                    if (exists.get(i)) {
                        redisConn.zScore(keys.get(i), shortId);
                    }
                }
                return null;
            }
        });
        List<TimeDataPoint> results = new ArrayList<TimeDataPoint>(keys.size());
        List<Long> timestamps = getTimestamps(interval, from, to);
        Iterator<Object> countsIterator = counts.iterator();
        for (int i = 0; i < timestamps.size(); i++) {
            if (exists.get(i)) {
                results.add(new TimeDataPoint(
                        timestamps.get(i).longValue(),
                        Long.toString(((Double)countsIterator.next()).longValue())));
            }
        }
        return Collections.unmodifiableList(results);
    }

    @Override
    public List<CountDataPoint> topCounts(String metricId, Interval interval, DateTime timestamp, long offset, long size) {
        final String key = getKey(metricId, interval, timestamp);
        Collection<TypedTuple<String>> data = zsetOps.reverseRangeWithScores(key, offset, offset + size - 1);
        List<CountDataPoint> results = new ArrayList<CountDataPoint>();
        for (TypedTuple<String> tuple : data) {
            results.add(new CountDataPoint(
                    nameIdDao.getName(tuple.getValue()), // Get back the original id
                    tuple.getScore().longValue()));
        }
        return results;
    }

    @Override
    public List<TimeDataPoint> uniqueCounts(String metricId, Interval interval, DateTime from, DateTime to) {
        final List<String> keys = getKeys(metricId, interval, from, to);
        final List<Boolean> exists = keyExists(keys);
        List<TimeDataPoint> results = new ArrayList<TimeDataPoint>(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            if (exists.get(i)) {
                String count = zsetOps.size(keys.get(i)).toString();
                long timestamp = PosixTimeUtil.floorToDay(from) * 1000L;
                results.add(new TimeDataPoint(timestamp, count));
            }
        }
        return Collections.unmodifiableList(results);
    }

    private void put(String metricId, String id, Interval interval, DateTime timestamp) {
        String key = getKey(metricId, interval, timestamp);
        zsetOps.incrementScore(key, id, 1.0d);
        redisTemplate.expire(key, EXPIRE_DAYS, TimeUnit.DAYS);
    }

    private List<Boolean> keyExists(final List<String> keys) {
        List<Object> objects = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection conn) throws DataAccessException {
                StringRedisConnection redisConn = (StringRedisConnection)conn;
                for (String key : keys) {
                    redisConn.exists(key);
                }
                return null;
            }
        });
        List<Boolean> flags = new ArrayList<Boolean>(objects.size());
        for (Object obj : objects) {
            flags.add((Boolean)obj);
        }
        return flags;
    }

    private List<String> getKeys(String metricId, Interval interval, DateTime from, DateTime to) {
        List<Long> flooredTimestamps = getTimestamps(interval, from, to);
        List<String> keys = new ArrayList<String>(flooredTimestamps.size());
        for (Long timestamp : flooredTimestamps) {
            keys.add(getKey(metricId, interval, timestamp.longValue()));
        }
        return keys;
    }

    private List<Long> getTimestamps(Interval interval, DateTime from, DateTime to) {
        switch (interval) {
            case day:
                return KEY_ASSEMBLER_DAY.getTimestamps(from, to);
            case week:
                return KEY_ASSEMBLER_WEEK.getTimestamps(from, to);
            case month:
                return KEY_ASSEMBLER_MONTH.getTimestamps(from, to);
            default:
                throw new IllegalArgumentException("Interval " + interval + " is not supported.");
        }
    }

    private String getKey(String metricId, Interval interval, long flooredPosixTime) {
        switch (interval) {
            case day:
                return KEY_ASSEMBLER_DAY.getKey(metricId, flooredPosixTime);
            case week:
                return KEY_ASSEMBLER_WEEK.getKey(metricId, flooredPosixTime);
            case month:
                return KEY_ASSEMBLER_MONTH.getKey(metricId, flooredPosixTime);
            default:
                throw new IllegalArgumentException("Interval " + interval + " is not supported.");
        }
    }

    private String getKey(String metricId, Interval interval, DateTime timestamp) {
        switch (interval) {
            case day:
                return KEY_ASSEMBLER_DAY.getKey(metricId, PosixTimeUtil.floorToDay(timestamp));
            case week:
                return KEY_ASSEMBLER_WEEK.getKey(metricId, PosixTimeUtil.floorToWeek(timestamp));
            case month:
                return KEY_ASSEMBLER_MONTH.getKey(metricId, PosixTimeUtil.floorToMonth(timestamp));
            default:
                throw new IllegalArgumentException("Interval " + interval + " is not supported.");
        }
    }

    private static final KeyAssembler KEY_ASSEMBLER_DAY = new KeyAssembler(n, day, uniquecount);
    private static final KeyAssembler KEY_ASSEMBLER_WEEK = new KeyAssembler(n, week, uniquecount);
    private static final KeyAssembler KEY_ASSEMBLER_MONTH = new KeyAssembler(n, month, uniquecount);

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource(name="redisTemplate")
    private ZSetOperations<String, String> zsetOps;

    @Resource
    private NameIdDao nameIdDao;
}
