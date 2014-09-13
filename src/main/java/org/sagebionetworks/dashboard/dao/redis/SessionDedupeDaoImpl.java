package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.Key.SEPARATOR;
import static org.sagebionetworks.dashboard.dao.redis.Key.SESSION_DEDUPE;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SessionDedupeDao;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository("sessionDedupeDao")
public class SessionDedupeDaoImpl implements SessionDedupeDao {

    @Override
    public boolean isProcessed(String sessionId) {
        final String key = SESSION_DEDUPE + SEPARATOR + sessionId;
        final boolean processed = !valOps.setIfAbsent(key, "true");
        redisTemplate.expire(key, 120, TimeUnit.MINUTES);
        return processed;
    }

    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valOps;

    @Resource
    private StringRedisTemplate redisTemplate;
}
