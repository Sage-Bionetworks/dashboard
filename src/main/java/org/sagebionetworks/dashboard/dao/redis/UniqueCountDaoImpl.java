package org.sagebionetworks.dashboard.dao.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.sagebionetworks.dashboard.model.redis.Aggregation;
import org.sagebionetworks.dashboard.model.redis.KeyAssembler;
import org.sagebionetworks.dashboard.model.redis.NameSpace;
import org.sagebionetworks.dashboard.model.redis.Statistic;
import org.sagebionetworks.dashboard.util.PosixTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

@Repository("uniqueCountDao")
public class UniqueCountDaoImpl implements UniqueCountDao {

    @Autowired
    private NameIdDao nameIdDao;

    @Override
    public void addMetric(final String metricId, final DateTime timestamp, final String id) {
        final String key = getKey(metricId, timestamp);
        final String newId = nameIdDao.getId(id); // Swap for a shorter id
        zsetOps.incrementScore(key, newId, 1.0d);
    }

    @Override
    public List<CountDataPoint> getMetric(String metricId, DateTime timestamp, final long n) {
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
    public long getUniqueCount(String metricId, DateTime timestamp) {
        final String key = getKey(metricId, timestamp);
        Long size = zsetOps.size(key);
        return (size == null ? 0 : size.longValue());
    }

    private String getKey(String metricId, DateTime timestamp) {
        return KEY_ASSEMBLER.getKey(metricId, PosixTimeUtil.floorToDay(timestamp));
    }

    private static final KeyAssembler KEY_ASSEMBLER = new KeyAssembler(
            Statistic.n,
            Aggregation.day,
            NameSpace.uniquecount);

    @Resource(name="redisTemplate")
    private ZSetOperations<String, String> zsetOps;
}
