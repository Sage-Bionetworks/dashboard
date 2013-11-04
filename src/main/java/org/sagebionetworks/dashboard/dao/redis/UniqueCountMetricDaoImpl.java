package org.sagebionetworks.dashboard.dao.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.UniqueCountMetricDao;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.sagebionetworks.dashboard.model.redis.Aggregation;
import org.sagebionetworks.dashboard.model.redis.KeyAssembler;
import org.sagebionetworks.dashboard.model.redis.NameSpace;
import org.sagebionetworks.dashboard.model.redis.Statistic;
import org.sagebionetworks.dashboard.util.PosixTimeUtil;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

@Repository("uniqueCountDao")
public class UniqueCountMetricDaoImpl implements UniqueCountMetricDao {

    @Override
    public void addMetric(String metricId, DateTime timestamp, String id) {
        final String key = getKey(metricId, timestamp);
        zsetOps.incrementScore(key, id, 1.0d);
    }

    @Override
    public List<CountDataPoint> getMetric(String metricId, DateTime timestamp) {
        final String key = getKey(metricId, timestamp);
        Collection<TypedTuple<String>> data = zsetOps.reverseRangeWithScores(key, 0L, Long.MAX_VALUE);
        List<CountDataPoint> results = new ArrayList<CountDataPoint>();
        for (TypedTuple<String> tuple : data) {
            results.add(new CountDataPoint(
                    tuple.getValue(),
                    Long.toString(tuple.getScore().longValue())));
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
            NameSpace.count);

    @Resource(name="redisTemplate")
    private ZSetOperations<String, String> zsetOps;
}
