package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Reader;
import java.io.StringReader;
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
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.parse.RecordParser;
import org.sagebionetworks.dashboard.parse.RepoRecordParser;
import org.sagebionetworks.dashboard.service.MetricReader;
import org.sagebionetworks.dashboard.service.UniqueCountWriter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class UniqueTableMetricTest {

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource
    private UniqueCountWriter<AccessRecord> uniqueCountWriter;

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
    public void validUri() {
        RecordParser parser = new RepoRecordParser();
        String line = ",\"17\",\"1423611312727\",,\"repo-prod-76-0.prod.sagebase.org\",\"33860\",\"Synpase-Java-Client/2015-01-30-1577-40e5630  Synapse-Web-Client/76.0-2-g5f246c4\",\"domain=SYNAPSE\",\"6a7616fd-9549-432f-9174-8005ba0d5665\",,\"/repo/v1/entity/syn3163713/table/query/async/get/29639\",\"3323072\",,\"2015-02-10\",\"GET\",\"043ad075351c10aa:4ea2e7a8:14b461f6975:-7ffd\",\"000000076\",\"prod\",\"true\",\"201\"";
        Reader reader = new StringReader(line);
        List<AccessRecord> records = parser.parse(reader);
        assertNotNull(records);
        assertEquals(1, records.size());
        Metric<AccessRecord, String> metric = new UniqueTableMetric();
        uniqueCountWriter.writeMetric(records.get(0), metric);

        DateTime dtFrom = new DateTime(2015, 02, 9, 0, 0);
        DateTime dtTo = new DateTime(2015, 02, 11, 0, 0);
        List<TimeDataPoint> results = metricReader.getUniqueCount(metric.getName(), Interval.day, dtFrom, dtTo);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1423526400000L, results.get(0).timestamp());
        assertEquals("1", results.get(0).value());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidUri() {

        RecordParser parser = new RepoRecordParser();
        String line = "\"1\",\"3\",\"1403557060405\",,\"repo-prod.prod.sagebase.org\",\"25808\",\"Synpase-Java-Client/develop-SNAPSHOT  Synapse-Web-Client/develop-SNAPSHOT\",\"domain=SYNAPSE\",\"b6415a25-e71a-4de9-8a1f-c26873a0449d\",,\"/repo/v1/certifiedUserTestResponse\",\"1118328\",,\"2014-06-23\",\"GET\",\"def12efa1aaf9fe8:2a2ab516:146a8217e19:-7ffd\",\"000000047\",\"prod\",\"true\",\"200\"";

        Reader reader = new StringReader(line);
        List<AccessRecord> records = parser.parse(reader);
        assertNotNull(records);
        assertEquals(1, records.size());
        Metric<AccessRecord, String> metric = new UniqueTableMetric();
        uniqueCountWriter.writeMetric(records.get(0), metric);

        DateTime dtFrom = new DateTime(2014, 06, 1, 0, 0);
        DateTime dtTo = new DateTime(2014, 06, 30, 0, 0);
        metricReader.getUniqueCount(metric.getName(), Interval.day, dtFrom, dtTo);
    }
}
