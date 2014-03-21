package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.dashboard.dao.SimpleCountDao;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.springframework.data.redis.core.StringRedisTemplate;

public class SimpleCountDaoImplTest extends AbstractRedisDaoTest {

    @Resource
    private SimpleCountDao simpleCountDao;

    @Resource
    private StringRedisTemplate redisTemplate;

    @Before
    public void before() {
        assertNotNull(simpleCountDao);
    }

    @Test
    public void test() {
        String metricId1 = "m1";
        String metricId2 = "m2";
        DateTime dt = new DateTime(2013, 12, 10, 17, 11, DateTimeZone.UTC);
        simpleCountDao.put(metricId1, dt);
        simpleCountDao.put(metricId1, dt.plusHours(1));
        simpleCountDao.put(metricId1, dt.plusDays(1));
        simpleCountDao.put(metricId2, dt.plusDays(1));
        List<TimeDataPoint> results = simpleCountDao.get(metricId1, dt, dt.plusDays(2));
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("2", results.get(0).value());
        assertEquals("1", results.get(1).value());
        results = simpleCountDao.get(metricId2, dt, dt.plusDays(2));
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("1", results.get(0).value());
    }

    @Test
    public void testExpire() {
        String metricId = getClass().getName() + ".testExpire";
        simpleCountDao.put(metricId, new DateTime(2013, 12, 10, 17, 11, DateTimeZone.UTC));
        Set<String> keys = redisTemplate.keys("*" + metricId + "*");
        for (String key : keys) {
            long expires = redisTemplate.getExpire(key, TimeUnit.DAYS);
            assertTrue(expires == 200L || expires == 199L);
        }
    }
}
