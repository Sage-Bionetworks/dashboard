package org.sagebionetworks.dashboard.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringReader;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.dashboard.dao.CachedDao;
import org.sagebionetworks.dashboard.dao.KeyCachedDao;
import org.sagebionetworks.dashboard.dao.redis.RedisTestUtil;
import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.metric.UniqueUserMetric;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.RecordParser;
import org.sagebionetworks.dashboard.parse.RepoRecordParser;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class MetricReaderTest {

    @Resource
    private KeyCachedDao keyDao;

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource
    private UniqueCountWriter<AccessRecord> uniqueCountWriter;

    @Resource
    private MetricReader metricReader;

    @Resource
    private CachedDao nameIdDao;

    private Metric<AccessRecord, String> metric = new UniqueUserMetric();

    @Before
    public void before() {
        assertNotNull(redisTemplate);
        assertNotNull(uniqueCountWriter);
        assertNotNull(metricReader);
        assertNotNull(nameIdDao);
        RedisTestUtil.clearRedis(redisTemplate, nameIdDao);
    }

    private void writeSomeRecords() {
        RecordParser parser = new RepoRecordParser();
        String line1 = ",\"17\",\"1423611312727\",,\"repo-prod-76-0.prod.sagebase.org\",\"33860\",\"Synpase-Java-Client/2015-01-30-1577-40e5630  Synapse-Web-Client/76.0-2-g5f246c4\",\"domain=SYNAPSE\",\"6a7616fd-9549-432f-9174-8005ba0d5665\",,\"/repo/v1/entity/syn3163713/table/upload/csv/async/start\",\"3323072\",,\"2015-02-10\",\"POST\",\"043ad075351c10aa:4ea2e7a8:14b461f6975:-7ffd\",\"000000076\",\"prod\",\"true\",\"201\"";
        String line2 = ",\"17\",\"1423611312727\",,\"repo-prod-76-0.prod.sagebase.org\",\"33860\",\"Synpase-Java-Client/2015-01-30-1577-40e5630  Synapse-Web-Client/76.0-2-g5f246c4\",\"domain=SYNAPSE\",\"6a7616fd-9549-432f-9174-8005ba0d5665\",,\"/repo/v1/entity/syn3163713/table/upload/csv/async/start\",\"3323072\",,\"2015-02-10\",\"POST\",\"043ad075351c10aa:4ea2e7a8:14b461f6974:-7ffd\",\"000000076\",\"prod\",\"true\",\"201\"";
        String line3 = ",\"17\",\"1423611312727\",,\"repo-prod-76-0.prod.sagebase.org\",\"33860\",\"Synpase-Java-Client/2015-01-30-1577-40e5630  Synapse-Web-Client/76.0-2-g5f246c4\",\"domain=SYNAPSE\",\"6a7616fd-9549-432f-9174-8005ba0d5665\",,\"/repo/v1/entity/syn3163713/table/upload/csv/async/start\",\"3323071\",,\"2015-02-10\",\"POST\",\"043ad075351c10aa:4ea2e7a8:14b461f6973:-7ffd\",\"000000076\",\"prod\",\"true\",\"201\"";
        String line4 = ",\"17\",\"1423611312727\",,\"repo-prod-76-0.prod.sagebase.org\",\"33860\",\"Synpase-Java-Client/2015-01-30-1577-40e5630  Synapse-Web-Client/76.0-2-g5f246c4\",\"domain=SYNAPSE\",\"6a7616fd-9549-432f-9174-8005ba0d5665\",,\"/repo/v1/entity/syn3163713/table/upload/csv/async/start\",\"3323072\",,\"2015-02-10\",\"POST\",\"043ad075351c10aa:4ea2e7a8:14b461f6972:-7ffd\",\"000000076\",\"prod\",\"true\",\"201\"";
        String line5 = ",\"17\",\"1423611312727\",,\"repo-prod-76-0.prod.sagebase.org\",\"33860\",\"Synpase-Java-Client/2015-01-30-1577-40e5630  Synapse-Web-Client/76.0-2-g5f246c4\",\"domain=SYNAPSE\",\"6a7616fd-9549-432f-9174-8005ba0d5665\",,\"/repo/v1/entity/syn3163713/table/upload/csv/async/start\",\"3323072\",,\"2015-02-10\",\"POST\",\"043ad075351c10aa:4ea2e7a8:14b461f6971:-7ffd\",\"000000076\",\"prod\",\"true\",\"201\"";
        List<AccessRecord> records = parser.parse(new StringReader(line1));
        assertNotNull(records);
        assertEquals(1, records.size());
        uniqueCountWriter.writeMetric(records.get(0), metric);
        records = parser.parse(new StringReader(line2));
        uniqueCountWriter.writeMetric(records.get(0), metric);
        records = parser.parse(new StringReader(line3));
        uniqueCountWriter.writeMetric(records.get(0), metric);
        records = parser.parse(new StringReader(line4));
        uniqueCountWriter.writeMetric(records.get(0), metric);
        records = parser.parse(new StringReader(line5));
        uniqueCountWriter.writeMetric(records.get(0), metric);
    }

    @After
    public void after() {
        RedisTestUtil.clearRedis(redisTemplate, nameIdDao);
    }

    @Test
    public void test() {
        // write a few records to Unique User Metric
        writeSomeRecords(); 
        // test getTotalCount
        assertEquals(metricReader.getTotalCount(metric.getName()), "2");
        // test getSessionCount
        assertEquals("5", metricReader.getSessionCount(metric.getName(),
                Interval.month, new DateTime(2015, 02, 01, 0, 0),
                new DateTime(2015, 02, 28, 0, 0)));
    }
}
