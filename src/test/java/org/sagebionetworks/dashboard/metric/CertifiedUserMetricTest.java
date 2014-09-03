package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.dashboard.RedisTestUtil;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.sagebionetworks.dashboard.parse.CuPassingRecord;
import org.sagebionetworks.dashboard.service.MetricReader;
import org.sagebionetworks.dashboard.service.UniqueCountWriter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CertifiedUserMetricTest {

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource
    private UniqueCountWriter uniqueCountWriter;

    @Resource
    private MetricReader metricReader;

    @Before
    public void before() {
        assertNotNull(redisTemplate);
        assertNotNull(uniqueCountWriter);
        RedisTestUtil.clearRedis(redisTemplate);
    }

    @After
    public void after() {
        RedisTestUtil.clearRedis(redisTemplate);
    }

    @Test
    public void testPassedRecord() {

        CuPassingRecord record = new CuPassingRecord(true, null, new DateTime(2014, 6, 27, 12, 0, 0, 0), 10);

        assertNotNull(record);
        CertifiedUserMetric metric = new CertifiedUserMetric();
        uniqueCountWriter.writeCertifiedUsersMetric(record, metric);

        DateTime dtFrom = new DateTime(2014, 01, 1, 0, 0);
        DateTime dtTo = new DateTime(2014, 07, 30, 0, 0);
        List<TimeDataPoint> results = metricReader.getUniqueCount(metric.getName(), Interval.day, dtFrom, dtTo);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1403827200000L, results.get(0).timestamp());
        assertEquals("1", results.get(0).value());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFailedRecord() {

        CuPassingRecord record = new CuPassingRecord(false, null, new DateTime(2014, 5, 20, 12, 0, 0, 0), 10);

        assertNotNull(record);
        CertifiedUserMetric metric = new CertifiedUserMetric();
        uniqueCountWriter.writeCertifiedUsersMetric(record, metric);

        DateTime dtFrom = new DateTime(2014, 01, 1, 0, 0);
        DateTime dtTo = new DateTime(2014, 07, 30, 0, 0);
        metricReader.getUniqueCount(metric.getName(), Interval.day, dtFrom, dtTo);
    }
}
