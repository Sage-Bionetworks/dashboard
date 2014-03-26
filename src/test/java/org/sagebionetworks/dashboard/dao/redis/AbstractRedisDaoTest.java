package org.sagebionetworks.dashboard.dao.redis;

import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractRedisDaoTest {

    private static final ConfigurableApplicationContext context =
            new ClassPathXmlApplicationContext("/META-INF/spring/app-context.xml");

    @BeforeClass
    public static void beforeClass() {
        context.registerShutdownHook();
        // Test that the Redis connection exists before testing anything else
        StringRedisTemplate redisTemplate = getRedisTemplate();
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
        clearRedis();
    }

    @AfterClass
    public static void afterClass() {
        clearRedis();
    }

    private static StringRedisTemplate getRedisTemplate() {
        // Manually load the context since this is within a static method
        StringRedisTemplate redisTemplate = context.getBean(StringRedisTemplate.class);
        return redisTemplate;
    }

    private static void clearRedis() {
        // Remove all the keys
        StringRedisTemplate redisTemplate = getRedisTemplate();
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            redisTemplate.delete(key);
            Assert.assertFalse(redisTemplate.hasKey(key));
        }
    }
}
