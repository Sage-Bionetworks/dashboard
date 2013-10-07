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
    public void addMetric(final String clientId, final DateTime timestamp, final long latency) {
        addAggregation(minute_3, clientId, timestamp, latency);
        addAggregation(hour, clientId, timestamp, latency);
        addAggregation(day, clientId, timestamp, latency);
    }

    @Override
    public List<DataPoint> getMetrics(final String clientId, final DateTime from, final DateTime to,
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

    private void addAggregation(final Aggregation aggregation,
            final String clientId, final DateTime timestamp, final long latency) {

        // n
        String key = getKey(n, aggregation, clientId, timestamp);
        valueOps.increment(key, 1L);
        // sum
        key = getKey(sum, aggregation, clientId, timestamp);
        valueOps.increment(key, latency);
        // max -- best effort to avoid race conditions
        final String maxKey = getKey(max, aggregation, clientId, timestamp);
        String strMax = valueOps.get(maxKey);
        long currMax = strMax == null ? -1L : Long.parseLong(strMax);
        long newMax = latency;
        int i = 0;
        while (currMax < newMax && i < 5) {
            // in case some other client set the max in the middle
            strMax = valueOps.getAndSet(maxKey, Long.toString(newMax));
            currMax = newMax;
            newMax = strMax == null ? -1L : Long.parseLong(strMax);
            i++;
        }
    }

    private String getKey(final Statistic stat, final Aggregation aggr,
            final String clientId, final DateTime timestamp) {

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
        String key = keyAssembler.getKey(clientId, ts);
        redisTemplate.expire(key, EXPIRE_DAYS, TimeUnit.DAYS);
        return key;
    }

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOps;
}
