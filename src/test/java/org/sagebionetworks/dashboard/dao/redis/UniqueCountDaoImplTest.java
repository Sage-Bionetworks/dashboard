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
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.sagebionetworks.dashboard.util.PosixTimeUtil;
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
        final DateTime day1 = new DateTime(2011, 11, 15, 8, 51, DateTimeZone.UTC); // Monday
        final DateTime day2 = new DateTime(2011, 11, 16, 8, 51, DateTimeZone.UTC); // Tuesday
        final DateTime day3 = new DateTime(2011, 11, 22, 8, 51, DateTimeZone.UTC); // next Monday
        final DateTime day4 = new DateTime(2012, 1, 22, 8, 51, DateTimeZone.UTC);  // next year, for testing the month interval

        // Set up the following counts:
        // (m1, id1, day1) = 1
        // (m2, id2, day1) = 3
        // (m2, id3, day1) = 2
        // (m2, id1, day1) = 1
        // (m2, id1, day2) = 2
        // (m2, id1, day3) = 5
        uniqueCountDao.put(m1, id1, day1);
        uniqueCountDao.put(m2, id3, day1);
        uniqueCountDao.put(m2, id2, day1);
        uniqueCountDao.put(m2, id2, day1);
        uniqueCountDao.put(m2, id2, day1);
        uniqueCountDao.put(m2, id3, day1);
        uniqueCountDao.put(m2, id1, day1);
        uniqueCountDao.put(m2, id1, day2);
        uniqueCountDao.put(m2, id1, day2);
        uniqueCountDao.put(m2, id1, day3);
        uniqueCountDao.put(m2, id1, day3);
        uniqueCountDao.put(m2, id1, day3);
        uniqueCountDao.put(m2, id1, day3);
        uniqueCountDao.put(m2, id1, day3);
        uniqueCountDao.put(m2, id1, day4);

        // m1 by day
        List<CountDataPoint> counts = uniqueCountDao.getTop(m1, Interval.day, day1, 0L, 100L);
        assertNotNull(counts);
        assertEquals(1, counts.size());
        assertEquals(id1, counts.get(0).id());
        assertEquals(1L, counts.get(0).count());

        // m2 by month Top 1
        counts = uniqueCountDao.getTop(m2, Interval.month, day2, 0L, 1L);
        assertEquals(1, counts.size());
        assertEquals(id1, counts.get(0).id());
        assertEquals(8L, counts.get(0).count());
        // Top 2
        counts = uniqueCountDao.getTop(m2, Interval.month, day2, 0L, 2L);
        assertEquals(2, counts.size());
        assertEquals(id1, counts.get(0).id());
        assertEquals(8L, counts.get(0).count());
        assertEquals(id2, counts.get(1).id());
        assertEquals(3L, counts.get(1).count());
        // Next page
        counts = uniqueCountDao.getTop(m2, Interval.month, day2, 2L, 1000000L);
        assertEquals(1, counts.size());
        assertEquals(id3, counts.get(0).id());
        assertEquals(2L, counts.get(0).count());

        // m2 id1 counts by week
        List<TimeDataPoint> dataPoints = uniqueCountDao.get(m2, id1, Interval.week, day1, day3);
        assertNotNull(dataPoints);
        assertEquals(2, dataPoints.size());
        assertEquals(PosixTimeUtil.floorToWeek(day1) * 1000L, dataPoints.get(0).timestamp());
        assertEquals("3", dataPoints.get(0).value());
        assertEquals(PosixTimeUtil.floorToWeek(day3) * 1000L, dataPoints.get(1).timestamp());
        assertEquals("5", dataPoints.get(1).value());

        // unique counts
        dataPoints = uniqueCountDao.getUnique(m1, Interval.day, day1, day2);
        assertNotNull(dataPoints);
        assertEquals(1, dataPoints.size());
        assertEquals(PosixTimeUtil.floorToDay(day1) * 1000L, dataPoints.get(0).timestamp());
        assertEquals("1", dataPoints.get(0).value());
        dataPoints = uniqueCountDao.getUnique(m2, Interval.day, day1, day2);
        assertNotNull(dataPoints);
        assertEquals(2, dataPoints.size());
        assertEquals(PosixTimeUtil.floorToDay(day1) * 1000L, dataPoints.get(0).timestamp());
        assertEquals("3", dataPoints.get(0).value());
        assertEquals(PosixTimeUtil.floorToDay(day2) * 1000L, dataPoints.get(1).timestamp());
        assertEquals("1", dataPoints.get(1).value());
        dataPoints = uniqueCountDao.getUnique(m2, Interval.day, day1, day3);
        assertNotNull(dataPoints);
        assertEquals(3, dataPoints.size());
        dataPoints = uniqueCountDao.getUnique(m1, Interval.day, day1, day1);
        assertEquals(1, dataPoints.size());
        assertEquals(PosixTimeUtil.floorToDay(day1) * 1000L, dataPoints.get(0).timestamp());
        assertEquals("1", dataPoints.get(0).value());

        // Verify we get back an empty results set for a future time
        dataPoints = uniqueCountDao.getUnique(m1, Interval.day, day1.plusYears(1), day2.plusYears(1));
        assertNotNull(dataPoints);
        assertEquals(0, dataPoints.size());

        dataPoints = uniqueCountDao.getUnique(m2, Interval.month, day1, day4.plusMonths(1));
        assertNotNull(dataPoints);
        assertEquals(2, dataPoints.size());
    }

    @Test
    public void testKeyExpire() throws InterruptedException {

        final String metricId = getClass().getName() + ".testKeyExpire";
        final String id = "id";
        DateTime dt = new DateTime(2005, 9, 25, 9, 30, DateTimeZone.UTC);
        uniqueCountDao.put(metricId, id, dt);

        Set<String> keys = redisTemplate.keys("*" + metricId + "*");
        for (String key : keys) {
            long expires = redisTemplate.getExpire(key, TimeUnit.DAYS);
            assertTrue(expires == 200L || expires == 199L);
        }
    }
}
