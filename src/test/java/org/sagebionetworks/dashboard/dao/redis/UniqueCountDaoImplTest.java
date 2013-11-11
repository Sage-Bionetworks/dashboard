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
import org.sagebionetworks.dashboard.model.TimeDataPoint;
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

        // (m1, day1, id1) = 1
        // (m2, day1, id2) = 3
        // (m2, day1, id3) = 2
        // (m2, day1, id1) = 1
        // (m2, day2, id1) = 2
        uniqueCountDao.add(m1, day1, id1);
        uniqueCountDao.add(m2, day1, id3);
        uniqueCountDao.add(m2, day1, id2);
        uniqueCountDao.add(m2, day1, id2);
        uniqueCountDao.add(m2, day1, id2);
        uniqueCountDao.add(m2, day1, id3);
        uniqueCountDao.add(m2, day1, id1);
        uniqueCountDao.add(m2, day2, id1);
        uniqueCountDao.add(m2, day2, id1);

        List<CountDataPoint> results = uniqueCountDao.topCounts(m1, day1, Long.MAX_VALUE);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(id1, results.get(0).getId());
        assertEquals(1L, results.get(0).getCount());

        // Only look at top 1
        results = uniqueCountDao.topCounts(m2, day1, 1L);
        assertEquals(1, results.size());
        assertEquals(id2, results.get(0).getId());
        assertEquals(3L, results.get(0).getCount());
        // Top 2
        results = uniqueCountDao.topCounts(m2, day1, 2L);
        assertEquals(2, results.size());
        assertEquals(id2, results.get(0).getId());
        assertEquals(3L, results.get(0).getCount());
        assertEquals(id3, results.get(1).getId());
        assertEquals(2L, results.get(1).getCount());
        results = uniqueCountDao.topCounts(m2, day1, Long.MAX_VALUE);
        assertEquals(3, results.size());
        assertEquals(id2, results.get(0).getId());
        assertEquals(3L, results.get(0).getCount());
        assertEquals(id3, results.get(1).getId());
        assertEquals(2L, results.get(1).getCount());
        assertEquals(id1, results.get(2).getId());
        assertEquals(1L, results.get(2).getCount());
        // Verify day 2 results for m2
        results = uniqueCountDao.topCounts(m2, day2, Long.MAX_VALUE);
        assertEquals(1, results.size());
        assertEquals(id1, results.get(0).getId());
        assertEquals(2L, results.get(0).getCount());

        // Verify results as time series
        List<TimeDataPoint> dataPoints = uniqueCountDao.uniqueCounts(m1, day1, day2);
        assertNotNull(dataPoints);
        assertEquals(2, dataPoints.size());
        assertEquals("1", dataPoints.get(0).getValue());
        DateTime d1 = new DateTime(dataPoints.get(0).getTimestampInMs());
        assertEquals("0", dataPoints.get(1).getValue());
        DateTime d2 = new DateTime(dataPoints.get(1).getTimestampInMs());
        assertEquals(1, d2.getDayOfYear() - d1.getDayOfYear());
        dataPoints = uniqueCountDao.uniqueCounts(m2, day1, day2);
        assertNotNull(dataPoints);
        assertEquals(2, dataPoints.size());
        assertEquals("3", dataPoints.get(0).getValue());
        assertEquals("1", dataPoints.get(1).getValue());
        dataPoints = uniqueCountDao.uniqueCounts(m1, day1, day1);
        assertEquals(1, dataPoints.size());
        assertEquals("1", dataPoints.get(0).getValue());
    }

    @Test
    public void testKeyExpire() throws InterruptedException {

        final String metricId = this.getClass().getName() + ".testKeyExpire";
        final String id = "id";
        DateTime dt = new DateTime(2005, 9, 25, 9, 30, DateTimeZone.UTC);
        uniqueCountDao.add(metricId, dt, id);

        Set<String> keys = redisTemplate.keys("*" + metricId + "*");
        for (String key : keys) {
            long expires = redisTemplate.getExpire(key, TimeUnit.DAYS);
            assertTrue(expires == 180L || expires == 179L);
        }
    }
}
