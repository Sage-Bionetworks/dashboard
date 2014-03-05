package org.sagebionetworks.dashboard.dao.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.SimpleCountDao;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.Statistic;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository("simpleCountDao")
public class SimpleCountDaoImpl implements SimpleCountDao {

    @Override
    public void put(String metricId, DateTime timestamp) {
        String key = KEY_ASSEMBLER.getKey(metricId, timestamp);
        valueOps.increment(key, 1L);
    }

    @Override
    public List<TimeDataPoint> get(String metricId, DateTime from, DateTime to) {

        List<Long> timestamps = KEY_ASSEMBLER.getPosixTimestamps(from, to);
        List<String> keys = new ArrayList<String>();
        for (Long timestamp : timestamps) {
            keys.add(KEY_ASSEMBLER.getKey(metricId, timestamp.longValue()));
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

    private static final KeyAssembler KEY_ASSEMBLER = new KeyAssembler(
            Statistic.n,
            Interval.day,
            NameSpace.count);

    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOps;
}
