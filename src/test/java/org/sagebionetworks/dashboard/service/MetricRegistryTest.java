package org.sagebionetworks.dashboard.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.metric.TimeSeriesToWrite;
import org.sagebionetworks.dashboard.metric.UniqueCountToWrite;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class MetricRegistryTest {

    @Before
    public void before() {
        assertNotNull(redisTemplate);
        assertNotNull(nameIdDao);
        assertNotNull(registry);
        clearRedis();
    }

    @After
    public void after() {
        clearRedis();
    }

    @Test
    public void test() {
        List<MetricToRead> metrics = registry.metricsToRead();
        assertNotNull(metrics);
        assertEquals(6, metrics.size());
        assertEquals("Newly Registered Users", metrics.get(0).getName());
        assertNotNull(metrics.get(0).getId());
        assertFalse(metrics.get(0).getId().equals(nameIdDao.getId(metrics.get(0).getName())));
        Collection<UniqueCountToWrite> uniqueCounts = registry.uniqueCountToWrite();
        assertNotNull(uniqueCounts);
        assertEquals(7, uniqueCounts.size());
        Collection<TimeSeriesToWrite> timeSeries = registry.timeSeriesToWrite();
        assertNotNull(timeSeries);
        assertEquals(1, timeSeries.size());
        MetricToRead metric2 = metrics.get(2);
        assertEquals(metric2, registry.getMetric(metric2.getId(), metric2.getType()));
    }

    private void clearRedis() {
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            redisTemplate.delete(key);
            assertFalse(redisTemplate.hasKey(key));
        }
    }

    @Resource
    private MetricRegistry registry;

    @Resource
    private NameIdDao nameIdDao;

    @Resource
    private StringRedisTemplate redisTemplate;
}
