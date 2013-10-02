package org.sagebionetworks.dashboard.dao.redis;

import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

// The context below will be inherited by all the child classes
@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractRedisDaoTest {

    @BeforeClass
    public static void beforeClass() {
        // Test that the Redis connection exists before testing anything else
        RedisTemplate<String, String> redisTemplate = getRedisTemplate();
        redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                try {
                    String pong = connection.ping();
                    return pong;
                } catch (RedisConnectionFailureException e) {
                    String msg = "Please make sure a local Redis server is running at 127.0.0.1:6379";
                    Assert.fail(msg);
                    return msg;
                }
            }
        });
    }

    @AfterClass
    public static void afterClass() {
        // Remove all the keys
        RedisTemplate<String, String> redisTemplate = getRedisTemplate();
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            redisTemplate.delete(key);
            Assert.assertFalse(redisTemplate.hasKey(key));
        }
    }

    private static RedisTemplate<String, String> getRedisTemplate() {
        // Manually load the context since this is within a static method
        ApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] {"/META-INF/spring/test-context.xml"});
        @SuppressWarnings("unchecked")
        RedisTemplate<String, String> redisTemplate = context.getBean(RedisTemplate.class);
        return redisTemplate;
    }
}
