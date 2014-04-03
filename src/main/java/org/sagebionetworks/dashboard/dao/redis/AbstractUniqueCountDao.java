package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.NameSpace.uniquecount;
import static org.sagebionetworks.dashboard.model.Interval.day;
import static org.sagebionetworks.dashboard.model.Interval.month;
import static org.sagebionetworks.dashboard.model.Interval.week;
import static org.sagebionetworks.dashboard.model.Statistic.n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

abstract class AbstractUniqueCountDao implements UniqueCountDao {

    @Override
    public List<TimeDataPoint> get(String metricId, String id, Interval interval, DateTime from, DateTime to) {
        final String shortId = nameIdDao.getId(id); // Swap for a shorter id
        final List<KeyPiece> keys = getKeys(metricId, interval, from, to);
        final List<KeyPiece> existingKeys = getExistingKeys(keys);
        List<Object> counts = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection conn) throws DataAccessException {
                StringRedisConnection redisConn = (StringRedisConnection)conn;
                for (KeyPiece k : existingKeys) {
                    redisConn.zScore(k.key, shortId);
                }
                return null;
            }
        });
        if (existingKeys.size() != counts.size()) {
            throw new RuntimeException("Impedence mismatch between the list of existing keys and the list of counts.");
        }
        List<TimeDataPoint> results = new ArrayList<TimeDataPoint>(existingKeys.size());
        for (int i = 0; i < existingKeys.size(); i++) {
            Double count = (Double)counts.get(i);
            count = (count == null ? Double.valueOf(0.0) : count);
            results.add(new TimeDataPoint(
                    existingKeys.get(i).posixTime * 1000L,
                    Long.toString(count.longValue())));
        }
        return Collections.unmodifiableList(results);
    }

    @Override
    public List<CountDataPoint> getTop(String metricId, Interval interval, DateTime timestamp, long offset, long size) {
        final String key = getKey(metricId, interval, timestamp);
        Collection<TypedTuple<String>> data = zsetOps.reverseRangeWithScores(key, offset, offset + size - 1);
        List<CountDataPoint> results = new ArrayList<CountDataPoint>();
        for (TypedTuple<String> tuple : data) {
            results.add(new CountDataPoint(
                    nameIdDao.getName(tuple.getValue()), // Get back the original id
                    tuple.getScore().longValue()));
        }
        return Collections.unmodifiableList(results);
    }

    @Override
    public List<TimeDataPoint> getUnique(String metricId, Interval interval, DateTime from, DateTime to) {
        return getUnique(metricId, interval, from, to, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Override
    public List<TimeDataPoint> getUnique(String metricId, Interval interval, DateTime from, DateTime to, final long min, final long max) {
        final List<KeyPiece> keys = getKeys(metricId, interval, from, to);
        final List<KeyPiece> existingKeys = getExistingKeys(keys);
        List<Object> counts = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection conn) throws DataAccessException {
                StringRedisConnection redisConn = (StringRedisConnection)conn;
                boolean zCard = (Long.MIN_VALUE == min && Long.MAX_VALUE == max);
                for (KeyPiece k : existingKeys) {
                    if (zCard) {
                        redisConn.zCard(k.key); // O(1)
                    } else {
                        redisConn.zCount(k.key, min, max); // O(n)
                    }
                }
                return null;
            }
        });
        if (existingKeys.size() != counts.size()) {
            throw new RuntimeException("Impedence mismatch between the list of existing keys and the list of counts.");
        }
        List<TimeDataPoint> results = new ArrayList<TimeDataPoint>(existingKeys.size());
        for (int i = 0; i < existingKeys.size(); i++) {
            results.add(new TimeDataPoint(
                    existingKeys.get(i).posixTime * 1000L,
                    ((Long)counts.get(i)).toString()));
        }
        return Collections.unmodifiableList(results);
    }

    protected String getKey(String metricId, Interval interval, DateTime timestamp) {
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

    private List<KeyPiece> getExistingKeys(final List<KeyPiece> keys) {
        List<Object> flags = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection conn) throws DataAccessException {
                StringRedisConnection redisConn = (StringRedisConnection)conn;
                for (KeyPiece k : keys) {
                    redisConn.exists(k.key);
                }
                return null;
            }
        });
        if (flags.size() != keys.size()) {
            throw new RuntimeException("Impedence mismatch between the list of keys and their boolean flags.");
        }
        List<KeyPiece> existing = new ArrayList<KeyPiece>(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            if ((Boolean)flags.get(i)) {
                existing.add(keys.get(i));
            }
        }
        return existing;
    }

    private List<KeyPiece> getKeys(String metricId, Interval interval, DateTime from, DateTime to) {
        List<Long> posixTimestamps = getPosixTimestamps(interval, from, to);
        List<KeyPiece> keys = new ArrayList<KeyPiece>(posixTimestamps.size());
        for (Long timestamp : posixTimestamps) {
            keys.add(new KeyPiece(getKey(metricId, interval, timestamp.longValue()), timestamp));
        }
        return keys;
    }

    private List<Long> getPosixTimestamps(Interval interval, DateTime from, DateTime to) {
        switch (interval) {
            case day:
                return KEY_ASSEMBLER_DAY.getPosixTimestamps(from, to);
            case week:
                return KEY_ASSEMBLER_WEEK.getPosixTimestamps(from, to);
            case month:
                return KEY_ASSEMBLER_MONTH.getPosixTimestamps(from, to);
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

    private static final KeyAssembler KEY_ASSEMBLER_DAY = new KeyAssembler(n, day, uniquecount);
    private static final KeyAssembler KEY_ASSEMBLER_WEEK = new KeyAssembler(n, week, uniquecount);
    private static final KeyAssembler KEY_ASSEMBLER_MONTH = new KeyAssembler(n, month, uniquecount);

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource(name="redisTemplate")
    private ZSetOperations<String, String> zsetOps;

    @Resource
    private NameIdDao nameIdDao;

    private static class KeyPiece {
        private String key;
        private long posixTime;
        private KeyPiece (String key, long posixTime) {
            this.key = key;
            this.posixTime = posixTime;
        }
    }
}
