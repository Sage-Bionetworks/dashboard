package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.sagebionetworks.dashboard.model.redis.Aggregation.day;
import static org.sagebionetworks.dashboard.model.redis.Aggregation.hour;
import static org.sagebionetworks.dashboard.model.redis.Aggregation.minute_3;
import static org.sagebionetworks.dashboard.model.redis.Statistic.avg;
import static org.sagebionetworks.dashboard.model.redis.Statistic.max;
import static org.sagebionetworks.dashboard.model.redis.Statistic.n;
import static org.sagebionetworks.dashboard.model.redis.Statistic.sum;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.dashboard.dao.TimeSeriesDao;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.springframework.beans.factory.annotation.Autowired;

public class TimeSeriesDaoImplTest extends AbstractRedisDaoTest {

    @Autowired
    private TimeSeriesDao timeSeriesDao;

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
        timeSeriesDao.addMetric(python, dt, latency);

        latency = 5L;
        dt = dt.plusMinutes(1);
        timeSeriesDao.addMetric(python, dt, latency);

        latency = 1L;
        dt = dt.plusMinutes(11);
        timeSeriesDao.addMetric(python, dt, latency);

        latency = 7L;
        dt = dt.plusHours(1);
        timeSeriesDao.addMetric(python, dt, latency);

        latency = 2L;
        dt = dt.plusMinutes(2);
        timeSeriesDao.addMetric(python, dt, latency);

        latency = 3L;
        dt = dt.plusDays(3);
        timeSeriesDao.addMetric(python, dt, latency);

        // this one is out of range
        latency = 13L;
        dt = dt.plusMonths(1);
        timeSeriesDao.addMetric(python, dt, latency);

        // Metrics for R
        final String r = "r";
        latency = 101L;
        dt = new DateTime(2005, 2, 21, 11, 32, 51, 31, DateTimeZone.UTC);
        timeSeriesDao.addMetric(r, dt, latency);

        latency = 79L;
        dt = dt.plusHours(1);
        timeSeriesDao.addMetric(r, dt, latency);

        final DateTime start = new DateTime(2005, 2, 1, 0, 0, 0, 0, DateTimeZone.UTC);
        final DateTime end = new DateTime(2005, 3, 1, 0, 0, 0, 0, DateTimeZone.UTC);
        List<TimeDataPoint> sumListM3 = timeSeriesDao.getMetric(python, start, end, sum, minute_3);
        assertEquals(4, sumListM3.size());
        TimeDataPoint dp = sumListM3.get(0);
        assertEquals("1107855000", dp.getTimestamp());
        assertEquals("16", dp.getValue());
        dp = sumListM3.get(1);
        assertEquals("1107855720", dp.getTimestamp());
        assertEquals("1", dp.getValue());
        dp = sumListM3.get(2);
        assertEquals("1107859320", dp.getTimestamp());
        assertEquals("9", dp.getValue());
        dp = sumListM3.get(3);
        assertEquals("1108118520", dp.getTimestamp());
        assertEquals("3", dp.getValue());

        List<TimeDataPoint> sumListHour = timeSeriesDao.getMetric(python, start, end, sum, hour);
        assertEquals(3, sumListHour.size());
        dp = sumListHour.get(0);
        assertEquals("1107853200", dp.getTimestamp());
        assertEquals("17", dp.getValue());
        dp = sumListHour.get(1);
        assertEquals("1107856800", dp.getTimestamp());
        assertEquals("9", dp.getValue());
        dp = sumListHour.get(2);
        assertEquals("1108116000", dp.getTimestamp());
        assertEquals("3", dp.getValue());

        List<TimeDataPoint> sumListDay = timeSeriesDao.getMetric(python, start, end, sum, day);
        assertEquals(2, sumListDay.size());
        dp = sumListDay.get(0);
        assertEquals("1107820800", dp.getTimestamp());
        assertEquals("26", dp.getValue());
        dp = sumListDay.get(1);
        assertEquals("1108080000", dp.getTimestamp());
        assertEquals("3", dp.getValue());

        List<TimeDataPoint> nListDay = timeSeriesDao.getMetric(python, start, end, n, day);
        assertEquals(2, nListDay.size());
        dp = nListDay.get(0);
        assertEquals("1107820800", dp.getTimestamp());
        assertEquals("5", dp.getValue());
        dp = nListDay.get(1);
        assertEquals("1108080000", dp.getTimestamp());
        assertEquals("1", dp.getValue());

        List<TimeDataPoint> maxListDay = timeSeriesDao.getMetric(python, start, end, max, day);
        assertEquals(2, maxListDay.size());
        dp = maxListDay.get(0);
        assertEquals("1107820800", dp.getTimestamp());
        assertEquals("11", dp.getValue());
        dp = maxListDay.get(1);
        assertEquals("1108080000", dp.getTimestamp());
        assertEquals("3", dp.getValue());

        List<TimeDataPoint> avgListDay = timeSeriesDao.getMetric(python, start, end, avg, day);
        assertEquals(2, avgListDay.size());
        dp = avgListDay.get(0);
        assertEquals("1107820800", dp.getTimestamp());
        assertEquals("5", dp.getValue());
        dp = avgListDay.get(1);
        assertEquals("1108080000", dp.getTimestamp());
        assertEquals("3", dp.getValue());
    }
}
