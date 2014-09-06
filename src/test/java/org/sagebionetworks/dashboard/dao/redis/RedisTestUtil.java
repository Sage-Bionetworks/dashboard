package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertFalse;

import java.util.Set;

import org.sagebionetworks.dashboard.dao.CachedDao;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisTestUtil {

    public static void clearRedis(StringRedisTemplate redisTemplate, CachedDao... caches) {
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            redisTemplate.delete(key);
            assertFalse(redisTemplate.hasKey(key));
        }
        for (CachedDao cache : caches) {
            cache.clearCache();
        }
    }
}
