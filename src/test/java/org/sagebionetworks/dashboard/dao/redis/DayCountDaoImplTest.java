package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    @Test
    public void testMultiThread() throws Exception {

        // Generate 200 random integers between 1 and 31
        Random random = new Random();
        final int[] days = new int[200];
        Arrays.fill(days, -1);
        int i = 0;
        while (i < 200) {
            int day = (int)(random.nextGaussian() * 5.0 + 15.0);
            if (day > 0 && day < 32) {
                days[i] = day;
                i++;
            }
        }

        // How many unique days -- this will be the expected results
        final boolean[] uniqueDays = new boolean[32];
        Arrays.fill(uniqueDays, false);
        for (int day : days) {
            uniqueDays[day] = true;
        }
        int uniqueDayCount = 0;
        for (boolean day : uniqueDays) {
            if (day) {
                uniqueDayCount++;
            }
        }

        // Create 200 random timestamps
        final List<DateTime> timestamps = new ArrayList<DateTime>(200);
        for (int day : days) {
            timestamps.add(new DateTime(2005, 1, day, 3, 21, 39, DateTimeZone.UTC));
        }

        // Create 100 tasks each gets a randomly shuffled list to work on
        List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>(100);
        for (int j = 0; j < 10; j++) {
            tasks.add(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    List<DateTime> myTimestamps = new ArrayList<DateTime>(200);
                    myTimestamps.addAll(timestamps);
                    Collections.shuffle(myTimestamps);
                    final String metric = getClass().getName();
                    final String id = "testMultiThread";
                    for (DateTime timestamp : myTimestamps) {
                        dayCountDao.put(metric, id, timestamp);
                    }
                    List<TimeDataPoint> results = dayCountDao.get(metric, id, Interval.month, timestamps.get(0), timestamps.get(0));
                    return Integer.valueOf(results.get(0).y());
                }                
            });
        }

        // Execute the 100 tasks concurrently and check the results
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        try {
            List<Future<Integer>> futures = threadPool.invokeAll(tasks);
            for (Future<Integer> future : futures) {
                while (!future.isDone()) {
                    Thread.sleep(10L);
                }
                assertEquals(uniqueDayCount, future.get().intValue());
            }
        } finally {
            threadPool.shutdown();
        }
    }
}
