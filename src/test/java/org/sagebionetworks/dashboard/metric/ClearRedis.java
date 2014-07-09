package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertFalse;

import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;

public class ClearRedis {

    public static void clearRedis(StringRedisTemplate redisTemplate) {
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            redisTemplate.delete(key);
            assertFalse(redisTemplate.hasKey(key));
        }
    }
}