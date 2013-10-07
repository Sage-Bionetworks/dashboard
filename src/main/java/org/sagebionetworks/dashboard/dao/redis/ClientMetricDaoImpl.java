package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.model.redis.Aggregation.day;
import static org.sagebionetworks.dashboard.model.redis.Aggregation.hour;
import static org.sagebionetworks.dashboard.model.redis.Aggregation.minute_3;
import static org.sagebionetworks.dashboard.model.redis.NameSpace.client;
import static org.sagebionetworks.dashboard.model.redis.RedisConstants.EXPIRE_DAYS;
import static org.sagebionetworks.dashboard.model.redis.Statistic.max;
import static org.sagebionetworks.dashboard.model.redis.Statistic.n;
import static org.sagebionetworks.dashboard.model.redis.Statistic.sum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.ClientMetricDao;
import org.sagebionetworks.dashboard.model.DataPoint;
import org.sagebionetworks.dashboard.model.redis.Aggregation;
import org.sagebionetworks.dashboard.model.redis.KeyAssembler;
import org.sagebionetworks.dashboard.model.redis.Statistic;
import org.sagebionetworks.dashboard.util.PosixTimeUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository("clientMetricDao")
public class ClientMetricDaoImpl implements ClientMetricDao {

    @Override
    public void addLatencyMetric(final String metricId, final DateTime timestamp, final long latency) {
        addAggregation(minute_3, metricId, timestamp, latency);
        addAggregation(hour, metricId, timestamp, latency);
        addAggregation(day, metricId, timestamp, latency);
    }

    @Override
    public List<DataPoint> getMetric(final String metricId, final DateTime from, final DateTime to,
            final Statistic statistic, final Aggregation aggregation) {

        long start = -1L;
        long end = -1L;
        long step = -1L;
        switch (aggregation) {
            case minute_3:
                 start = PosixTimeUtil.floorToMinute3(from);
                 end = PosixTimeUtil.floorToMinute3(to);
                 step = PosixTimeUtil.MINUTE_3;
                 break;
            case hour:
                start = PosixTimeUtil.floorToHour(from);
                end = PosixTimeUtil.floorToHour(to);
                step = PosixTimeUtil.HOUR;
                break;
            case day:
                start = PosixTimeUtil.floorToDay(from);
                end = PosixTimeUtil.floorToDay(to);
                step = PosixTimeUtil.DAY;
                break;
            default:
                throw new RuntimeException("Aggregation " + aggregation + " not supported.");
        }

        KeyAssembler keyAssembler = new KeyAssembler(statistic, aggregation, client);
        List<String> timestamps = new ArrayList<String>();
        List<String> keys = new ArrayList<String>();
        for (long i = start; i <= end; i += step) {
            timestamps.add(Long.toString(i));
            keys.add(keyAssembler.getKey(metricId, i));
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

    private void addAggregation(final Aggregation aggregation,
            final String metricId, final DateTime timestamp, final long latency) {

        // n
        String key = getKey(n, aggregation, metricId, timestamp);
        valueOps.increment(key, 1L);
        // sum
        key = getKey(sum, aggregation, metricId, timestamp);
        valueOps.increment(key, latency);
        // max -- optimistically work around race conditions
        final String maxKey = getKey(max, aggregation, metricId, timestamp);
        String strMax = valueOps.get(maxKey);
        long redisMax = strMax == null ? -1L : Long.parseLong(strMax);
        long max = latency;
        int i = 0;
        while (redisMax < max && i < 5) {
            // in case some other client set the max in the middle
            strMax = valueOps.getAndSet(maxKey, Long.toString(max));
            redisMax = max;
            max = strMax == null ? -1L : Long.parseLong(strMax);
            i++;
        }
        if (redisMax < max) {
            throw new RuntimeException(
                    "Failed to set the max after 5 retries due to possible race conditions.");
        }
    }

    private String getKey(final Statistic stat, final Aggregation aggr,
            final String metricId, final DateTime timestamp) {

        long ts = -1L;
        switch(aggr) {
            case minute_3:
                ts = PosixTimeUtil.floorToMinute3(timestamp);
                break;
            case hour:
                ts = PosixTimeUtil.floorToHour(timestamp);
                break;
            case day:
                ts = PosixTimeUtil.floorToDay(timestamp);
                break;
            default:
                throw new RuntimeException("Aggregation " + aggr + " not supported.");
        }

        KeyAssembler keyAssembler = new KeyAssembler(stat, aggr, client);
        String key = keyAssembler.getKey(metricId, ts);
        redisTemplate.expire(key, EXPIRE_DAYS, TimeUnit.DAYS);
        return key;
    }

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOps;
}
