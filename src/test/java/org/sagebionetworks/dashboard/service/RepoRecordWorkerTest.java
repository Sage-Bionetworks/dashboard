package org.sagebionetworks.dashboard.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RepoRecordWorkerTest {

    @Before
    public void before() {
        assertNotNull(repoRecordWorker);
        assertNotNull(redisTemplate);
        clearRedis();
    }

    @After
    public void After() {
        clearRedis();
    }

    @Test
    public void test() {
        repoRecordWorker.doWork();
    }

    private void clearRedis() {
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            redisTemplate.delete(key);
            assertFalse(redisTemplate.hasKey(key));
        }
    }

    @Resource
    private RepoRecordWorker repoRecordWorker;

    @Resource
    private StringRedisTemplate redisTemplate;
}
