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
import org.sagebionetworks.dashboard.dao.CachedDao;
import org.sagebionetworks.dashboard.dao.redis.RedisTestUtil;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.sagebionetworks.dashboard.parse.CuResponseRecord;
import org.sagebionetworks.dashboard.service.MetricReader;
import org.sagebionetworks.dashboard.service.UniqueCountWriter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class QuestionFailMetricTest {


    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource
    private UniqueCountWriter<CuResponseRecord> uniqueCountWriter;

    @Resource
    private MetricReader metricReader;

    @Resource
    private CachedDao nameIdDao;

    @Before
    public void before() {
        assertNotNull(redisTemplate);
        assertNotNull(uniqueCountWriter);
        RedisTestUtil.clearRedis(redisTemplate, nameIdDao);
    }

    @After
    public void after() {
        RedisTestUtil.clearRedis(redisTemplate, nameIdDao);
    }

    @Test
    public void testIncorrectRecord() {

        CuResponseRecord record = new CuResponseRecord(1, 1, new DateTime(2014, 5, 20, 12, 0, 0, 0), false);

        assertNotNull(record);
        QuestionMetric metric = new QuestionFailMetric();
        uniqueCountWriter.writeResponse(record, metric, false);

        DateTime dtFrom = new DateTime(2014, 01, 1, 0, 0);
        DateTime dtTo = new DateTime(2014, 07, 30, 0, 0);
        List<TimeDataPoint> results = metricReader.getUniqueCount(metric.getName(), "1", Interval.day, dtFrom, dtTo);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1400544000000L, results.get(0).timestamp());
        assertEquals("1", results.get(0).value());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCorrectRecord() {

        CuResponseRecord record = new CuResponseRecord(1, 1, new DateTime(2014, 5, 20, 12, 0, 0, 0), true);

        assertNotNull(record);
        QuestionMetric metric = new QuestionFailMetric();
        uniqueCountWriter.writeResponse(record, metric, false);

        DateTime dtFrom = new DateTime(2014, 01, 1, 0, 0);
        DateTime dtTo = new DateTime(2014, 07, 30, 0, 0);
        metricReader.getUniqueCount(metric.getName(), "1", Interval.day, dtFrom, dtTo);
    }
}
