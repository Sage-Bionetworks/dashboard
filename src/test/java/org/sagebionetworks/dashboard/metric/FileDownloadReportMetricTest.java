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
import org.sagebionetworks.dashboard.RedisTestUtil;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.UserDataPoint;
import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordParser;
import org.sagebionetworks.dashboard.parse.RepoRecordParser;
import org.sagebionetworks.dashboard.service.MetricReader;
import org.sagebionetworks.dashboard.service.ReportWriter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class FileDownloadReportMetricTest {

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource
    private ReportWriter reportWriter;

    @Resource
    private MetricReader metricReader;

    @Before
    public void before() {
        assertNotNull(redisTemplate);
        assertNotNull(reportWriter);
        RedisTestUtil.clearRedis(redisTemplate);
    }

    @After
    public void after() {
        RedisTestUtil.clearRedis(redisTemplate);
    }

    @Test
    public void validUri() {
        RecordParser parser = new RepoRecordParser();
        String line = ",\"37\",\"1404855949372\",,\"repo-prod.prod.sagebase.org\",\"58400\",\"Synpase-Java-Client/48.0-9-gc12ca7b\",\"redirect=false\",\"b698f0fa-1b0a-4dc2-bdee-b1606a9dc881\",,\"/repo/v1/entity/syn1960975/version/1/file\",\"1584359\",,\"2014-07-08\",\"GET\",\"1562bb43b38576e9:30376851:146cb357393:-7ffd\",\"000000048\",\"prod\",\"true\",\"200\"";
        Reader reader = new StringReader(line);
        List<Record> records = parser.parse(reader);
        assertNotNull(records);
        assertEquals(1, records.size());
        Metric<String> metric = new FileDownloadReportMetric();
        reportWriter.writeMetric(records.get(0), metric);
        
        List<UserDataPoint> results = 
                metricReader.getFileDownloadReport(metric.getName(), "1960975", 
                        new DateTime(1404855949372L), Interval.day);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("1404855949372", results.get(0).timestamp());
        assertEquals("1584359", results.get(0).userId());
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidUri() {
        RecordParser parser = new RepoRecordParser();
        String line = ",\"37\",\"1404855949372\",,\"repo-prod.prod.sagebase.org\",\"58400\",\"Synpase-Java-Client/48.0-9-gc12ca7b\",\"redirect=false\",\"b698f0fa-1b0a-4dc2-bdee-b1606a9dc881\",,\"/repo/v1/entity/syn1960975/bundle\",\"1584359\",,\"2014-07-08\",\"GET\",\"1562bb43b38576e9:30376851:146cb357393:-7ffd\",\"000000048\",\"prod\",\"true\",\"200\"";
        Reader reader = new StringReader(line);
        List<Record> records = parser.parse(reader);
        assertNotNull(records);
        assertEquals(1, records.size());
        Metric<String> metric = new FileDownloadReportMetric();
        reportWriter.writeMetric(records.get(0), metric);
        
        metricReader.getFileDownloadReport(metric.getName(), "1960975", 
                        new DateTime(1404855949372L), Interval.day);
    }
}
