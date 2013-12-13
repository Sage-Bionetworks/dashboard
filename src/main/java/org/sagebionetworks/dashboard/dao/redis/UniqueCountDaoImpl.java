package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.RedisConstants.EXPIRE_DAYS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.model.Aggregation;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.sagebionetworks.dashboard.model.Statistic;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.sagebionetworks.dashboard.util.PosixTimeUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

@Repository("uniqueCountDao")
public class UniqueCountDaoImpl implements UniqueCountDao {

    @Override
    public void put(final String metricId, final DateTime timestamp, final String id) {
        final String key = getKey(metricId, timestamp);
        final String newId = nameIdDao.getId(id); // Swap for a shorter id
        zsetOps.incrementScore(key, newId, 1.0d);
        redisTemplate.expire(key, EXPIRE_DAYS, TimeUnit.DAYS);
    }

    @Override
    public List<CountDataPoint> topCounts(String metricId, DateTime timestamp, final long n) {
        final String key = getKey(metricId, timestamp);
        long end = Long.MAX_VALUE == n ? Long.MAX_VALUE : (n - 1L); // end is inclusive
        if (end < 0L) {
            end = 0L;
        }
        Collection<TypedTuple<String>> data = zsetOps.reverseRangeWithScores(key, 0L, end);
        List<CountDataPoint> results = new ArrayList<CountDataPoint>();
        for (TypedTuple<String> tuple : data) {
            results.add(new CountDataPoint(
                    nameIdDao.getName(tuple.getValue()), // Get back the original id
                    tuple.getScore().longValue()));
        }
        return results;
    }

    @Override
    public List<TimeDataPoint> uniqueCounts(String metricId, DateTime from, DateTime to) {
        List<TimeDataPoint> results = new ArrayList<TimeDataPoint>();
        while (from.isBefore(to) || from.isEqual(to)) {
            final String key = getKey(metricId, from);
            Long count = zsetOps.size(key);
            count = (count == null ? Long.valueOf(0L) : count);
            long timestamp = PosixTimeUtil.floorToDay(from) * 1000L;
            TimeDataPoint dataPoint = new TimeDataPoint(timestamp, count.toString());
            results.add(dataPoint);
            from = from.plusDays(1);
        }
        return results;
    }

    private String getKey(String metricId, DateTime timestamp) {
        String key = KEY_ASSEMBLER.getKey(metricId, PosixTimeUtil.floorToDay(timestamp));
        return key;
    }

    private static final KeyAssembler KEY_ASSEMBLER = new KeyAssembler(
            Statistic.n,
            Aggregation.day,
            NameSpace.uniquecount);

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource(name="redisTemplate")
    private ZSetOperations<String, String> zsetOps;

    @Resource
    private NameIdDao nameIdDao;
}
