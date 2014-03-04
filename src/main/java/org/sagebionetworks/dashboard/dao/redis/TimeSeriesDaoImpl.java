package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.NameSpace.timeseries;
import static org.sagebionetworks.dashboard.dao.redis.RedisConstants.EXPIRE_DAYS;
import static org.sagebionetworks.dashboard.model.Interval.day;
import static org.sagebionetworks.dashboard.model.Interval.hour;
import static org.sagebionetworks.dashboard.model.Interval.m3;
import static org.sagebionetworks.dashboard.model.Statistic.max;
import static org.sagebionetworks.dashboard.model.Statistic.n;
import static org.sagebionetworks.dashboard.model.Statistic.sum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.TimeSeriesDao;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.Statistic;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository("timeSeriesDao")
public class TimeSeriesDaoImpl implements TimeSeriesDao {

    @Override
    public void put(final String metricId, final DateTime timestamp, final long value) {
        addInterval(m3, metricId, timestamp, value);
        addInterval(hour, metricId, timestamp, value);
        addInterval(day, metricId, timestamp, value);
    }

    @Override
    public List<TimeDataPoint> timeSeries(final String metricId, final DateTime from, final DateTime to,
            final Statistic statistic, final Interval interval) {

        // Average is derived from sum/n
        if (Statistic.avg.equals(statistic)) {
            return getAvg(metricId, from, to, interval);
        }

        KeyAssembler keyAssembler = new KeyAssembler(statistic, interval, timeseries);
        List<Long> timestamps = keyAssembler.getTimestamps(from, to);
        List<String> keys = new ArrayList<String>();
        for (Long timestamp : timestamps) {
            keys.add(keyAssembler.getKey(metricId, timestamp.longValue()));
        }

        List<String> values = valueOps.multiGet(keys);
        List<TimeDataPoint> data = new ArrayList<TimeDataPoint>();
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            if (value != null) {
                data.add(new TimeDataPoint(timestamps.get(i).longValue() * 1000L, value));
            }
        }

        return Collections.unmodifiableList(data);
    }

    private List<TimeDataPoint> getAvg(final String metricId, final DateTime from, final DateTime to,
            final Interval interval) {

        List<TimeDataPoint> sumList = timeSeries(metricId, from, to, sum, interval);
        List<TimeDataPoint> nList = timeSeries(metricId, from, to, n, interval);
        List<TimeDataPoint> avgList = new ArrayList<TimeDataPoint>(sumList.size());
        for (int i = 0; i < sumList.size(); i++) {
            TimeDataPoint iSum = sumList.get(i);
            TimeDataPoint iN = nList.get(i);
            long average = Long.parseLong(iSum.value()) / Long.parseLong(iN.value());
            avgList.add(new TimeDataPoint(iSum.timestamp(), Long.toString(average)));
        }
        return avgList;
    }

    private void addInterval(final Interval interval,
            final String metricId, final DateTime timestamp, final long value) {

        // n
        String key = (new KeyAssembler(n, interval, timeseries)).getKey(metricId, timestamp);
        valueOps.increment(key, 1L);
        redisTemplate.expire(key, EXPIRE_DAYS, TimeUnit.DAYS);
        // sum
        key = (new KeyAssembler(sum, interval, timeseries)).getKey(metricId, timestamp);
        valueOps.increment(key, value);
        redisTemplate.expire(key, EXPIRE_DAYS, TimeUnit.DAYS);
        // max -- optimistically work around race conditions
        final String maxKey = (new KeyAssembler(max, interval, timeseries)).getKey(metricId, timestamp);
        String strMax = valueOps.get(maxKey);
        long redisMax = strMax == null ? -1L : Long.parseLong(strMax);
        long max = value;
        int i = 0;
        while (redisMax < max && i < 5) {
            // in case some other client set the max in the middle
            strMax = valueOps.getAndSet(maxKey, Long.toString(max));
            redisTemplate.expire(maxKey, EXPIRE_DAYS, TimeUnit.DAYS);
            redisMax = max;
            max = strMax == null ? -1L : Long.parseLong(strMax);
            i++;
        }
        if (redisMax < max) {
            throw new RuntimeException(
                    "Failed to set the max after 5 retries due to possible race conditions.");
        }
    }

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOps;
}
