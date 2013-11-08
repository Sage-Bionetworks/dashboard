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
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.springframework.data.redis.core.StringRedisTemplate;

public class UniqueCountDaoImplTest extends AbstractRedisDaoTest {

    @Resource
    private UniqueCountDao uniqueCountDao;

    @Resource
    private StringRedisTemplate redisTemplate;

    @Before
    public void before() {
        assertNotNull(uniqueCountDao);
    }

    @Test
    public void test() {

        final String m1 = "metric1";
        final String m2 = "metric2";
        final String id1 = "id1";
        final String id2 = "id2";
        final String id3 = "id3";
        final DateTime day1 = new DateTime(2011, 11, 15, 8, 51, DateTimeZone.UTC);
        final DateTime day2 = new DateTime(2011, 11, 16, 8, 51, DateTimeZone.UTC);

        uniqueCountDao.addMetric(m1, day1, id1);
        assertEquals(1L, uniqueCountDao.getUniqueCount(m1, day1));
        assertEquals(0L, uniqueCountDao.getUniqueCount(m2, day1));
        List<CountDataPoint> results = uniqueCountDao.getMetric(m1, day1, Long.MAX_VALUE);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(id1, results.get(0).getId());
        assertEquals(1L, results.get(0).getCount());

        uniqueCountDao.addMetric(m2, day1, id3);
        uniqueCountDao.addMetric(m2, day1, id2);
        uniqueCountDao.addMetric(m2, day1, id2);
        uniqueCountDao.addMetric(m2, day1, id2);
        uniqueCountDao.addMetric(m2, day1, id3);
        uniqueCountDao.addMetric(m2, day1, id1);
        uniqueCountDao.addMetric(m2, day2, id1);
        uniqueCountDao.addMetric(m2, day2, id1);
        // Day 1 results for m1 should remain unchanged
        assertEquals(1L, uniqueCountDao.getUniqueCount(m1, day1));
        results = uniqueCountDao.getMetric(m1, day1, Long.MAX_VALUE);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(id1, results.get(0).getId());
        assertEquals(1L, results.get(0).getCount());
        // Verify day 1 results for m2
        assertEquals(3L, uniqueCountDao.getUniqueCount(m2, day1));
        // Only look at top 1
        results = uniqueCountDao.getMetric(m2, day1, 1L);
        assertEquals(1, results.size());
        assertEquals(id2, results.get(0).getId());
        assertEquals(3L, results.get(0).getCount());
        // Top 2
        results = uniqueCountDao.getMetric(m2, day1, 2L);
        assertEquals(2, results.size());
        assertEquals(id2, results.get(0).getId());
        assertEquals(3L, results.get(0).getCount());
        assertEquals(id3, results.get(1).getId());
        assertEquals(2L, results.get(1).getCount());
        results = uniqueCountDao.getMetric(m2, day1, Long.MAX_VALUE);
        assertEquals(3, results.size());
        assertEquals(id2, results.get(0).getId());
        assertEquals(3L, results.get(0).getCount());
        assertEquals(id3, results.get(1).getId());
        assertEquals(2L, results.get(1).getCount());
        assertEquals(id1, results.get(2).getId());
        assertEquals(1L, results.get(2).getCount());
        // Verify day 2 results for m2
        assertEquals(1L, uniqueCountDao.getUniqueCount(m2, day2));
        results = uniqueCountDao.getMetric(m2, day2, Long.MAX_VALUE);
        assertEquals(1, results.size());
        assertEquals(id1, results.get(0).getId());
        assertEquals(2L, results.get(0).getCount());
    }

    @Test
    public void testKeyExpire() throws InterruptedException {

        final String metricId = this.getClass().getName() + ".testKeyExpire";
        final String id = "id";
        DateTime dt = new DateTime(2005, 9, 25, 9, 30, DateTimeZone.UTC);
        uniqueCountDao.addMetric(metricId, dt, id);

        Set<String> keys = redisTemplate.keys("*" + metricId + "*");
        for (String key : keys) {
            long expires = redisTemplate.getExpire(key, TimeUnit.DAYS);
            assertTrue(expires == 180L || expires == 179L);
        }
    }
}
