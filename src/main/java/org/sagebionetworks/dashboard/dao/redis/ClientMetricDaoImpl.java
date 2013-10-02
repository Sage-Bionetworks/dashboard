package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.model.redis.RedisConstants.EXPIRE_DAYS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.ClientMetricDao;
import org.sagebionetworks.dashboard.model.DataPoint;
import org.sagebionetworks.dashboard.model.redis.RedisKey.Aggregation;
import org.sagebionetworks.dashboard.model.redis.RedisKey.Metric;
import org.sagebionetworks.dashboard.model.redis.RedisKey.NameSpace;
import org.sagebionetworks.dashboard.model.redis.RedisKeyAssembler;
import org.sagebionetworks.dashboard.util.PosixTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class ClientMetricDaoImpl implements ClientMetricDao {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOps;

    @Override
    public void addMetric(final String clientId, final long latency, final long posixTime) {
        addMetricAtAggregation(clientId, latency, posixTime, Aggregation.MINUTE3);
        addMetricAtAggregation(clientId, latency, posixTime, Aggregation.HOUR);
        addMetricAtAggregation(clientId, latency, posixTime, Aggregation.DAY);
    }

    private void addMetricAtAggregation(final String clientId, final long latency,
            final long posixTime, final String aggregation) {

        long timestamp = -1;
        if (Aggregation.MINUTE3.equals(aggregation)) {
            timestamp = PosixTimeUtil.floorToMinute3(posixTime);
        } else if (Aggregation.HOUR.equals(aggregation)) {
            timestamp = PosixTimeUtil.floorToHour(posixTime);
        } else if (Aggregation.DAY.equals(aggregation)) {
            timestamp = PosixTimeUtil.floorToDay(posixTime);
        }
        if (timestamp < 0) {
            throw new RuntimeException("Incorrect aggregation: " + aggregation);
        }

        RedisKeyAssembler keyAssembler = null;
        // n
        keyAssembler = new RedisKeyAssembler(Metric.N, aggregation, NameSpace.CLIENT);
        String key = keyAssembler.getKey(clientId, timestamp);
        if (!redisTemplate.hasKey(key)) {
            valueOps.set(key, Long.toString(1L), EXPIRE_DAYS, TimeUnit.DAYS);
        } else {
            valueOps.increment(key, 1L);
        }
        // sum
        keyAssembler = new RedisKeyAssembler(Metric.SUM, aggregation, NameSpace.CLIENT);
        key = keyAssembler.getKey(clientId, timestamp);
        if (!redisTemplate.hasKey(key)) {
            valueOps.set(key, Long.toString(latency), EXPIRE_DAYS, TimeUnit.DAYS);
        } else {
            valueOps.increment(key, latency);
        }
        // max
        keyAssembler = new RedisKeyAssembler(Metric.MAX, aggregation, NameSpace.CLIENT);
        key = keyAssembler.getKey(clientId, timestamp);
        if (!redisTemplate.hasKey(key)) {
            valueOps.set(key, Long.toString(latency), EXPIRE_DAYS, TimeUnit.DAYS);
        } else {
            String maxVal = valueOps.get(key);
            long max = Long.parseLong(maxVal);
            if (max < latency) {
                valueOps.set(key, Long.toString(latency));
            }
        }
    }

    @Override
    public List<DataPoint> getMetrics(final String clientId, final long posixStart, final long posixEnd,
            final String metric, final String aggregation) {

        long from = -1L;
        long to = -1L;
        long step = -1L;
        if (Aggregation.MINUTE3.equals(aggregation)) {
            from = PosixTimeUtil.floorToMinute3(posixStart);
            to = PosixTimeUtil.floorToMinute3(posixEnd);
            step = PosixTimeUtil.MINUTE_3;
        } else if (Aggregation.HOUR.equals(aggregation)) {
            from = PosixTimeUtil.floorToHour(posixStart);
            to = PosixTimeUtil.floorToHour(posixEnd);
            step = PosixTimeUtil.HOUR;
        } else if (Aggregation.DAY.equals(aggregation)) {
            from = PosixTimeUtil.floorToDay(posixStart);
            to = PosixTimeUtil.floorToDay(posixEnd);
            step = PosixTimeUtil.DAY;
        }
        if (from < 0) {
            throw new RuntimeException("Incorrect aggregation: " + aggregation);
        }

        RedisKeyAssembler keyAssembler = new RedisKeyAssembler(metric, aggregation, NameSpace.CLIENT);
        List<String> timestamps = new ArrayList<String>();
        List<String> keys = new ArrayList<String>();
        for (long i = from; i <= to; i += step) {
            timestamps.add(Long.toString(i));
            keys.add(keyAssembler.getKey(clientId, i));
        }

        List<String> values = valueOps.multiGet(keys);
        List<DataPoint> data = new ArrayList<DataPoint>();
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            if (value != null) {
                data.add(new DataPoint(value, timestamps.get(i)));
            }
        }

        return Collections.unmodifiableList(data);
    }
}
