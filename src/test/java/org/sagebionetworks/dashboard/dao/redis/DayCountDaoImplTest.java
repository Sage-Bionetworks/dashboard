package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.springframework.data.redis.core.StringRedisTemplate;

public class DayCountDaoImplTest extends AbstractRedisDaoTest {

    @Resource
    private UniqueCountDao dayCountDao;

    @Resource
    private StringRedisTemplate redisTemplate;

    @Before
    public void before() {
        assertNotNull(dayCountDao);
    }

    @Test
    public void test() {
        final String metricId = getClass().getName();
        final String id1 = "test1";
        // Tuesday
        final DateTime start = new DateTime(2009, 5, 25, 10, 52, DateTimeZone.UTC);
        final DateTime end = new DateTime(2009, 9, 25, 10, 52, DateTimeZone.UTC);
        DateTime timestamp = start;
        dayCountDao.put(metricId, id1, timestamp);
        List<TimeDataPoint> results = dayCountDao.get(metricId, id1, Interval.week, start, end);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("1", results.get(0).value());
        // Add the same day
        timestamp = timestamp.plusMinutes(1);
        dayCountDao.put(metricId, id1, timestamp);
        results = dayCountDao.get(metricId, id1, Interval.week, start, end);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("1", results.get(0).value());
        // Add Wednesday
        timestamp = timestamp.plusDays(1);
        dayCountDao.put(metricId, id1, timestamp);
        results = dayCountDao.get(metricId, id1, Interval.week, start, end);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("2", results.get(0).value());
        results = dayCountDao.get(metricId, id1, Interval.month, start, end);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("2", results.get(0).value());
        // Add one day of the next month
        timestamp = timestamp.plusMonths(1);
        dayCountDao.put(metricId, id1, timestamp);
        results = dayCountDao.get(metricId, id1, Interval.week, start, end);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("2", results.get(0).value());
        assertEquals("1", results.get(1).value());
        results = dayCountDao.get(metricId, id1, Interval.month, start, end);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("2", results.get(0).value());
        assertEquals("1", results.get(1).value());
        // Add start again shouldn't change anything
        dayCountDao.put(metricId, id1, start);
        results = dayCountDao.get(metricId, id1, Interval.week, start, end);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("2", results.get(0).value());
        assertEquals("1", results.get(1).value());
        results = dayCountDao.get(metricId, id1, Interval.month, start, end);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("2", results.get(0).value());
        assertEquals("1", results.get(1).value());
        // Add a different id shouldn't change the counts of the first id
        final String id2 = "test2";
        dayCountDao.put(metricId, id2, timestamp);
        results = dayCountDao.get(metricId, id1, Interval.week, start, end);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("2", results.get(0).value());
        assertEquals("1", results.get(1).value());
        results = dayCountDao.get(metricId, id1, Interval.month, start, end);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("2", results.get(0).value());
        assertEquals("1", results.get(1).value());
    }
}
