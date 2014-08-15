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
import org.sagebionetworks.dashboard.parse.Response;
import org.sagebionetworks.dashboard.service.MetricReader;
import org.sagebionetworks.dashboard.service.UniqueCountWriter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class QuestionPassMetricTest {


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
    public void testCorrectRecord() {

        Response record = new Response(1, true);

        assertNotNull(record);
        QuestionMetric metric = new QuestionPassMetric();
        uniqueCountWriter.writeResponse(record, metric, new DateTime(2014, 5, 20, 12, 0, 0, 0), "12345", true);

        DateTime dtFrom = new DateTime(2014, 01, 1, 0, 0);
        DateTime dtTo = new DateTime(2014, 07, 30, 0, 0);
        List<TimeDataPoint> results = metricReader.getUniqueCount(metric.getName(), "1", Interval.day, dtFrom, dtTo);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1400544000000L, results.get(0).timestamp());
        assertEquals("1", results.get(0).value());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIncorrectRecord() {

        Response record = new Response(1, false);

        assertNotNull(record);
        QuestionMetric metric = new QuestionPassMetric();
        uniqueCountWriter.writeResponse(record, metric, new DateTime(2014, 5, 20, 12, 0, 0, 0), "12345", true);

        DateTime dtFrom = new DateTime(2014, 01, 1, 0, 0);
        DateTime dtTo = new DateTime(2014, 07, 30, 0, 0);
        metricReader.getUniqueCount(metric.getName(), "1", Interval.day, dtFrom, dtTo);
    }
}
