package org.sagebionetworks.dashboard.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.dashboard.metric.ErrorCountMetric;
import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordParser;
import org.sagebionetworks.dashboard.parse.RepoRecordParser;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleCountWriterIntegTest {

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource
    private SimpleCountWriter simpleCountWriter;

    @Resource
    private MetricReader metricReader;

    @Before
    public void before() {
        assertNotNull(redisTemplate);
        assertNotNull(simpleCountWriter);
        clearRedis();
    }

    @After
    public void after() {
        clearRedis();
    }

    @Test
    public void test() {

        RecordParser parser = new RepoRecordParser();
        String line = ",\"330\",\"1388278872156\",,\"repo-prod.prod.sagebase.org\",\"50032\",\"python-requests/1.2.3 CPython/2.7.4 Linux/3.8.0-19-generic\",\"limit=20&offset=40\",\"37dbacaa-d508-40bc-95df-027669c1defa\",,\"/repo/v1/evaluation/1917804/submission/all\",\"1976831\",,\"2013-12-29\",\"GET\",\"596b6cd4fe9c6b87:2490c0ea:142eda5a7fd:-7ffd\",\"000000024\",\"prod\",\"false\"";
        Reader reader = new StringReader(line);
        List<Record> records = parser.parse(reader);
        assertNotNull(records);
        assertEquals(1, records.size());
        Metric<String> metric = new ErrorCountMetric();
        simpleCountWriter.writeMetric(records.get(0), metric);

        DateTime dtFrom = new DateTime(2013, 10, 1, 0, 0);
        DateTime dtTo = new DateTime(2013, 12, 31, 0, 0);
        List<TimeDataPoint> results = metricReader.getCount(metric.getName(), dtFrom, dtTo);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1388275200000L, results.get(0).timestamp());
        assertEquals("1", results.get(0).value());
    }

    private void clearRedis() {
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            redisTemplate.delete(key);
            assertFalse(redisTemplate.hasKey(key));
        }
    }
}
