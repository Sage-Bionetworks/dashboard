package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.*;

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
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordParser;
import org.sagebionetworks.dashboard.parse.RepoRecordParser;
import org.sagebionetworks.dashboard.service.MetricReader;
import org.sagebionetworks.dashboard.service.UniqueCountWriter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CertifiedUserQuizRequestMetricTest {

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
        clearRedis();
    }

    @After
    public void after() {
        clearRedis();
    }

    @Test
    public void testValidUri() {

        RecordParser parser = new RepoRecordParser();
        String line = "\"1\",\"3\",\"1403557060405\",,\"repo-prod.prod.sagebase.org\",\"25808\",\"Synpase-Java-Client/develop-SNAPSHOT  Synapse-Web-Client/develop-SNAPSHOT\",\"domain=SYNAPSE\",\"b6415a25-e71a-4de9-8a1f-c26873a0449d\",,\"/repo/v1/certifiedUserTest\",\"1118328\",,\"2014-06-23\",\"GET\",\"def12efa1aaf9fe8:2a2ab516:146a8217e19:-7ffd\",\"000000047\",\"prod\",\"true\",\"200\"";

        Reader reader = new StringReader(line);
        List<Record> records = parser.parse(reader);
        assertNotNull(records);
        assertEquals(1, records.size());
        Metric<String> metric = new CertifiedUserQuizRequestMetric();
        uniqueCountWriter.writeMetric(records.get(0), metric);

        DateTime dtFrom = new DateTime(2014, 06, 1, 0, 0);
        DateTime dtTo = new DateTime(2014, 06, 30, 0, 0);
        List<TimeDataPoint> results = metricReader.getUniqueCount(metric.getName(), Interval.day, dtFrom, dtTo);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1403481600000L, results.get(0).timestamp());
        assertEquals("1", results.get(0).value());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidUri() {

        RecordParser parser = new RepoRecordParser();
        String line = "\"1\",\"3\",\"1403557060405\",,\"repo-prod.prod.sagebase.org\",\"25808\",\"Synpase-Java-Client/develop-SNAPSHOT  Synapse-Web-Client/develop-SNAPSHOT\",\"domain=SYNAPSE\",\"b6415a25-e71a-4de9-8a1f-c26873a0449d\",,\"/repo/v1/certifiedUserTestResponse\",\"1118328\",,\"2014-06-23\",\"GET\",\"def12efa1aaf9fe8:2a2ab516:146a8217e19:-7ffd\",\"000000047\",\"prod\",\"true\",\"200\"";

        Reader reader = new StringReader(line);
        List<Record> records = parser.parse(reader);
        assertNotNull(records);
        assertEquals(1, records.size());
        Metric<String> metric = new CertifiedUserQuizRequestMetric();
        uniqueCountWriter.writeMetric(records.get(0), metric);

        DateTime dtFrom = new DateTime(2014, 06, 1, 0, 0);
        DateTime dtTo = new DateTime(2014, 06, 30, 0, 0);
        metricReader.getUniqueCount(metric.getName(), Interval.day, dtFrom, dtTo);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidMethod() {

        RecordParser parser = new RepoRecordParser();
        String line = "\"1\",\"3\",\"1403557060405\",,\"repo-prod.prod.sagebase.org\",\"25808\",\"Synpase-Java-Client/develop-SNAPSHOT  Synapse-Web-Client/develop-SNAPSHOT\",\"domain=SYNAPSE\",\"b6415a25-e71a-4de9-8a1f-c26873a0449d\",,\"/repo/v1/certifiedUserTest\",\"1118328\",,\"2014-06-23\",\"POST\",\"def12efa1aaf9fe8:2a2ab516:146a8217e19:-7ffd\",\"000000047\",\"prod\",\"true\",\"200\"";

        Reader reader = new StringReader(line);
        List<Record> records = parser.parse(reader);
        assertNotNull(records);
        assertEquals(1, records.size());
        Metric<String> metric = new CertifiedUserQuizRequestMetric();
        uniqueCountWriter.writeMetric(records.get(0), metric);

        DateTime dtFrom = new DateTime(2014, 06, 1, 0, 0);
        DateTime dtTo = new DateTime(2014, 06, 30, 0, 0);
        metricReader.getUniqueCount(metric.getName(), Interval.day, dtFrom, dtTo);
    }

    private void clearRedis() {
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            redisTemplate.delete(key);
            assertFalse(redisTemplate.hasKey(key));
        }
    }
}
