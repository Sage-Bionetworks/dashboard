package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.dashboard.dao.ClientMetricDao;
import org.sagebionetworks.dashboard.model.DataPoint;
import org.sagebionetworks.dashboard.model.redis.RedisKey.Aggregation;
import org.sagebionetworks.dashboard.model.redis.RedisKey.Statistic;
import org.springframework.beans.factory.annotation.Autowired;

public class ClientMetricDaoImplTest extends AbstractRedisDaoTest {

    @Autowired
    private ClientMetricDao clientMetricDao;

    @Before
    public void before() {
        assertNotNull(clientMetricDao);
    }

    @Test
    public void test() {

        // Metrics for Python
        final String python = "python";
        long latency = 11L;
        DateTime dt = new DateTime(2005, 2, 8, 9, 30, 51, 31, DateTimeZone.UTC);
        long posixTime = dt.getMillis() / 1000L;
        clientMetricDao.addMetric(python, latency, posixTime);

        latency = 5L;
        dt = dt.plusMinutes(1);
        posixTime = dt.getMillis() / 1000L;
        clientMetricDao.addMetric(python, latency, posixTime);

        latency = 1L;
        dt = dt.plusMinutes(11);
        posixTime = dt.getMillis() / 1000L;
        clientMetricDao.addMetric(python, latency, posixTime);

        latency = 7L;
        dt = dt.plusHours(1);
        posixTime = dt.getMillis() / 1000L;
        clientMetricDao.addMetric(python, latency, posixTime);

        latency = 2L;
        dt = dt.plusMinutes(2);
        posixTime = dt.getMillis() / 1000L;
        clientMetricDao.addMetric(python, latency, posixTime);

        latency = 3L;
        dt = dt.plusDays(3);
        posixTime = dt.getMillis() / 1000L;
        clientMetricDao.addMetric(python, latency, posixTime);

        // this one is out of range
        latency = 13L;
        dt = dt.plusMonths(1);
        posixTime = dt.getMillis() / 1000L;
        clientMetricDao.addMetric(python, latency, posixTime);

        // Metrics for R
        final String r = "r";
        latency = 101L;
        dt = new DateTime(2005, 2, 21, 11, 32, 51, 31, DateTimeZone.UTC);
        posixTime = dt.getMillis() / 1000L;
        clientMetricDao.addMetric(r, latency, posixTime);

        latency = 79L;
        dt = dt.plusHours(1);
        posixTime = dt.getMillis() / 1000L;
        clientMetricDao.addMetric(r, latency, posixTime);

        dt = new DateTime(2005, 2, 1, 0, 0, 0, 0, DateTimeZone.UTC);
        final long start = dt.getMillis() / 1000L;
        dt = new DateTime(2005, 3, 1, 0, 0, 0, 0, DateTimeZone.UTC);
        final long end = dt.getMillis() / 1000L;
        List<DataPoint> sumListM3 = clientMetricDao.getMetrics(python, start, end, Statistic.SUM, Aggregation.MINUTE3);
        assertEquals(4, sumListM3.size());
        DataPoint dp = sumListM3.get(0);
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

        List<DataPoint> sumListHour = clientMetricDao.getMetrics(python, start, end, Statistic.SUM, Aggregation.HOUR);
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

        List<DataPoint> sumListDay = clientMetricDao.getMetrics(python, start, end, Statistic.SUM, Aggregation.DAY);
        assertEquals(2, sumListDay.size());
        dp = sumListDay.get(0);
        assertEquals("1107820800", dp.getTimestamp());
        assertEquals("26", dp.getValue());
        dp = sumListDay.get(1);
        assertEquals("1108080000", dp.getTimestamp());
        assertEquals("3", dp.getValue());

        List<DataPoint> nListDay = clientMetricDao.getMetrics(python, start, end, Statistic.N, Aggregation.DAY);
        assertEquals(2, nListDay.size());
        dp = nListDay.get(0);
        assertEquals("1107820800", dp.getTimestamp());
        assertEquals("5", dp.getValue());
        dp = nListDay.get(1);
        assertEquals("1108080000", dp.getTimestamp());
        assertEquals("1", dp.getValue());

        List<DataPoint> maxListDay = clientMetricDao.getMetrics(python, start, end, Statistic.MAX, Aggregation.DAY);
        assertEquals(2, maxListDay.size());
        dp = maxListDay.get(0);
        assertEquals("1107820800", dp.getTimestamp());
        assertEquals("11", dp.getValue());
        dp = maxListDay.get(1);
        assertEquals("1108080000", dp.getTimestamp());
        assertEquals("3", dp.getValue());
    }
}
