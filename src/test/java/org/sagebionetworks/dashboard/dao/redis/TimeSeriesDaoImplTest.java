package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.sagebionetworks.dashboard.model.Aggregation.day;
import static org.sagebionetworks.dashboard.model.Aggregation.hour;
import static org.sagebionetworks.dashboard.model.Aggregation.m3;
import static org.sagebionetworks.dashboard.model.Statistic.avg;
import static org.sagebionetworks.dashboard.model.Statistic.max;
import static org.sagebionetworks.dashboard.model.Statistic.n;
import static org.sagebionetworks.dashboard.model.Statistic.sum;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.dashboard.dao.TimeSeriesDao;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.springframework.data.redis.core.StringRedisTemplate;

public class TimeSeriesDaoImplTest extends AbstractRedisDaoTest {

    @Resource
    private TimeSeriesDao timeSeriesDao;

    @Resource
    private StringRedisTemplate redisTemplate;

    @Before
    public void before() {
        assertNotNull(timeSeriesDao);
    }

    @Test
    public void test() {

        // Metrics for Python
        final String python = "python";
        long latency = 11L;
        DateTime dt = new DateTime(2005, 2, 8, 9, 30, 51, 31, DateTimeZone.UTC);
        timeSeriesDao.put(python, dt, latency);

        latency = 5L;
        dt = dt.plusMinutes(1);
        timeSeriesDao.put(python, dt, latency);

        latency = 1L;
        dt = dt.plusMinutes(11);
        timeSeriesDao.put(python, dt, latency);

        latency = 7L;
        dt = dt.plusHours(1);
        timeSeriesDao.put(python, dt, latency);

        latency = 2L;
        dt = dt.plusMinutes(2);
        timeSeriesDao.put(python, dt, latency);

        latency = 3L;
        dt = dt.plusDays(3);
        timeSeriesDao.put(python, dt, latency);

        // this one is out of range
        latency = 13L;
        dt = dt.plusMonths(1);
        timeSeriesDao.put(python, dt, latency);

        // Metrics for R
        final String r = "r";
        latency = 101L;
        dt = new DateTime(2005, 2, 21, 11, 32, 51, 31, DateTimeZone.UTC);
        timeSeriesDao.put(r, dt, latency);

        latency = 79L;
        dt = dt.plusHours(1);
        timeSeriesDao.put(r, dt, latency);

        final DateTime start = new DateTime(2005, 2, 1, 0, 0, 0, 0, DateTimeZone.UTC);
        final DateTime end = new DateTime(2005, 3, 1, 0, 0, 0, 0, DateTimeZone.UTC);
        List<TimeDataPoint> sumListM3 = timeSeriesDao.timeSeries(python, start, end, sum, m3);
        assertEquals(4, sumListM3.size());
        TimeDataPoint dp = sumListM3.get(0);
        assertEquals(1107855000000L, dp.getTimestampInMs());
        assertEquals("16", dp.getValue());
        dp = sumListM3.get(1);
        assertEquals(1107855720000L, dp.getTimestampInMs());
        assertEquals("1", dp.getValue());
        dp = sumListM3.get(2);
        assertEquals(1107859320000L, dp.getTimestampInMs());
        assertEquals("9", dp.getValue());
        dp = sumListM3.get(3);
        assertEquals(1108118520000L, dp.getTimestampInMs());
        assertEquals("3", dp.getValue());

        List<TimeDataPoint> sumListHour = timeSeriesDao.timeSeries(python, start, end, sum, hour);
        assertEquals(3, sumListHour.size());
        dp = sumListHour.get(0);
        assertEquals(1107853200000L, dp.getTimestampInMs());
        assertEquals("17", dp.getValue());
        dp = sumListHour.get(1);
        assertEquals(1107856800000L, dp.getTimestampInMs());
        assertEquals("9", dp.getValue());
        dp = sumListHour.get(2);
        assertEquals(1108116000000L, dp.getTimestampInMs());
        assertEquals("3", dp.getValue());

        List<TimeDataPoint> sumListDay = timeSeriesDao.timeSeries(python, start, end, sum, day);
        assertEquals(2, sumListDay.size());
        dp = sumListDay.get(0);
        assertEquals(1107820800000L, dp.getTimestampInMs());
        assertEquals("26", dp.getValue());
        dp = sumListDay.get(1);
        assertEquals(1108080000000L, dp.getTimestampInMs());
        assertEquals("3", dp.getValue());

        List<TimeDataPoint> nListDay = timeSeriesDao.timeSeries(python, start, end, n, day);
        assertEquals(2, nListDay.size());
        dp = nListDay.get(0);
        assertEquals(1107820800000L, dp.getTimestampInMs());
        assertEquals("5", dp.getValue());
        dp = nListDay.get(1);
        assertEquals(1108080000000L, dp.getTimestampInMs());
        assertEquals("1", dp.getValue());

        List<TimeDataPoint> maxListDay = timeSeriesDao.timeSeries(python, start, end, max, day);
        assertEquals(2, maxListDay.size());
        dp = maxListDay.get(0);
        assertEquals(1107820800000L, dp.getTimestampInMs());
        assertEquals("11", dp.getValue());
        dp = maxListDay.get(1);
        assertEquals(1108080000000L, dp.getTimestampInMs());
        assertEquals("3", dp.getValue());

        List<TimeDataPoint> avgListDay = timeSeriesDao.timeSeries(python, start, end, avg, day);
        assertEquals(2, avgListDay.size());
        dp = avgListDay.get(0);
        assertEquals(1107820800000L, dp.getTimestampInMs());
        assertEquals("5", dp.getValue());
        dp = avgListDay.get(1);
        assertEquals(1108080000000L, dp.getTimestampInMs());
        assertEquals("3", dp.getValue());
    }

    @Test
    public void testKeyExpire() throws InterruptedException {

        final String metricId = this.getClass().getName() + ".testKeyExpire";
        long val = 83L;
        DateTime dt = new DateTime(2005, 9, 25, 9, 30, DateTimeZone.UTC);
        timeSeriesDao.put(metricId, dt, val);

        Set<String> keys = redisTemplate.keys("*" + metricId + "*");
        for (String key : keys) {
            long expires = redisTemplate.getExpire(key, TimeUnit.DAYS);
            assertTrue(expires == 180L || expires == 179L);
        }
    }
}
