package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.dashboard.dao.CountDao;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.springframework.data.redis.core.StringRedisTemplate;

public class CountDaoImplTest extends AbstractRedisDaoTest {

    @Resource
    private CountDao countDao;

    @Resource
    private StringRedisTemplate redisTemplate;

    @Before
    public void before() {
        assertNotNull(countDao);
    }

    @Test
    public void test() {
        String metricId1 = "m1";
        String metricId2 = "m2";
        DateTime dt = new DateTime(2013, 12, 10, 17, 11, DateTimeZone.UTC);
        countDao.put(metricId1, dt);
        countDao.put(metricId1, dt.plusHours(1));
        countDao.put(metricId1, dt.plusDays(1));
        countDao.put(metricId2, dt.plusDays(1));
        List<TimeDataPoint> results = countDao.get(metricId1, dt, dt.plusDays(2));
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("2", results.get(0).getValue());
        assertEquals("1", results.get(1).getValue());
        results = countDao.get(metricId2, dt, dt.plusDays(2));
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("1", results.get(0).getValue());
    }
}
