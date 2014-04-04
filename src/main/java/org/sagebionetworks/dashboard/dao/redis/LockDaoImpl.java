package org.sagebionetworks.dashboard.dao.redis;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.LockDao;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository("lockDao")
public class LockDaoImpl implements LockDao {

    @Override
    public String acquire(final String lock) {
        final String etag = UUID.randomUUID().toString();
        Boolean isSet = valOps.setIfAbsent(lock, etag);
        if (isSet != null && isSet) {
            // We now own the lock
            redisTemplate.expire(lock, RedisConstants.LOCK_EXPIRE_MINUTES, TimeUnit.MINUTES);
            return etag;
        }
        // In the rare situation where somebody else holds the lock
        // but does not set expire (by accident or by intention),
        // we have the duty to set it here so that it properly expires.
        Long expire = redisTemplate.getExpire(lock);
        if (expire == null || expire.longValue() <= 0) {
            redisTemplate.expire(lock, RedisConstants.LOCK_EXPIRE_MINUTES, TimeUnit.MINUTES);
        }
        return null;
    }

    @Override
    public boolean release(final String lock, final String etag) {
        SessionCallback<Boolean> callback = new SessionCallback<Boolean>() {
            @Override
            public <K, V> Boolean execute(RedisOperations<K, V> operations) {
                @SuppressWarnings("unchecked")
                RedisOperations<String, String> ops = (RedisOperations<String, String>) operations;
                ops.watch(lock);
                final String etagRedis = ops.boundValueOps(lock).get();
                if (etag.equals(etagRedis)) {
                    ops.multi();
                    ops.delete(lock);
                    List<Object> results = ops.exec();
                    if (results != null) {
                        return Boolean.TRUE;
                    }
                }
                ops.unwatch();
                return Boolean.FALSE;
            }
        };
        return redisTemplate.execute(callback);
    }

    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valOps;

    @Resource
    private StringRedisTemplate redisTemplate;
}
