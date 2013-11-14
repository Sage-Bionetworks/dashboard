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
    public void After() {
        clearRedis();
    }

    @Test
    public void test() {
        List<MetricToRead> metrics = registry.metricsToRead();
        assertNotNull(metrics);
        assertEquals(5, metrics.size());
        assertEquals("Top Users", metrics.get(0).getName());
        assertNotNull(metrics.get(0).getId());
        assertFalse(metrics.get(0).getId().equals(nameIdDao.getId(metrics.get(0).getName())));
        Collection<UniqueCountToWrite> uniqueCounts = registry.uniqueCountToWrite();
        assertNotNull(uniqueCounts);
        assertEquals(3, uniqueCounts.size());
        Collection<TimeSeriesToWrite> timeSeries = registry.timeSeriesToWrite();
        assertNotNull(timeSeries);
        assertEquals(1, timeSeries.size());
        MetricToRead metric3 = metrics.get(3);
        assertEquals(metric3, registry.getMetric(metric3.getId(), metric3.getType()));
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
