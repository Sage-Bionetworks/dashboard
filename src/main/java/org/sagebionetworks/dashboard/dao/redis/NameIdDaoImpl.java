package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.perf.PerfConstants.FIELD_SEPARATOR_1;
import static org.sagebionetworks.dashboard.perf.PerfConstants.FIELD_SEPARATOR_2;
import static org.sagebionetworks.dashboard.dao.redis.Key.ID_NAME;
import static org.sagebionetworks.dashboard.dao.redis.Key.NAME_ID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.perf.PerfConstants;
import org.sagebionetworks.dashboard.util.RandomIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository("nameIdDao")
public class NameIdDaoImpl implements NameIdDao {

    private final Logger logger = LoggerFactory.getLogger(NameIdDaoImpl.class);

    @Override
    public String getId(final String name) {

        final long start = System.nanoTime();

        BoundHashOperations<String, String, String> nameIdHash = getNameIdHash();
        String id = nameIdHash.get(name);
        if (id == null) {
            return generateId(name);
        }

        logger.info(FIELD_SEPARATOR_1 + " getId: "
                + FIELD_SEPARATOR_2 + (System.nanoTime() - start));

        return id;
    }

    @Override
    public String getName(final String id) {
        BoundHashOperations<String, String, String> idNameHash = getIdNameHash();
        return idNameHash.get(id);
    }

    @Override
    public boolean hasId(String id) {
        BoundHashOperations<String, String, String> idNameHash = getIdNameHash();
        return idNameHash.hasKey(id);
    }

    @Override
    public boolean hasName(String name) {

        final long start = System.nanoTime();

        BoundHashOperations<String, String, String> nameIdHash = getNameIdHash();

        logger.info("hasName: " + (System.nanoTime() - start));

        return nameIdHash.hasKey(name);
    }

    private BoundHashOperations<String, String, String> getNameIdHash() {
        return redisTemplate.boundHashOps(NAME_ID);
    }

    private BoundHashOperations<String, String, String> getIdNameHash() {
        return redisTemplate.boundHashOps(ID_NAME);
    }

    /**
     * Generates a new ID for the name and updates the mappings.
     */
    private String generateId(final String name) {

        SessionCallback<String> callback = new SessionCallback<String>() {

            @Override
            public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {

                @SuppressWarnings("unchecked")
                RedisOperations<String, String> ops = (RedisOperations<String, String>) operations;
                List<Object> results = null;
                final int retryLimit = 10;
                final long delay = 10;
                int i = 0;
                while (results == null && i < retryLimit) {
                    try {
                        Thread.sleep(delay << i);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    results = tryGenerateId(ops);
                    i++;
                }
                if (results == null) {
                    throw new RuntimeException("ID generation for " + name +
                            " failed after max number of retries.");
                }
                BoundHashOperations<String, String, String> nameIdHash = ops.boundHashOps(NAME_ID);
                String id = nameIdHash.get(name);
                if (id == null) {
                    throw new RuntimeException("ID not set in Redis for " + name);
                }
                return id;
            }

            private List<Object> tryGenerateId(RedisOperations<String, String> ops) {

                BoundHashOperations<String, String, String> nameIdHash = ops.boundHashOps(NAME_ID);
                BoundHashOperations<String, String, String> idNameHash = ops.boundHashOps(ID_NAME);

                // Watch the keys for optimistic locking
                Collection<String> keys = new ArrayList<String>();
                keys.add(NAME_ID);
                keys.add(ID_NAME);
                ops.watch(keys);

                // If an ID is already assigned before watching, return it
                String id = nameIdHash.get(name);
                if (id != null) {
                    String name = idNameHash.get(id);
                    if (name != null) {
                        ops.unwatch();
                        return Collections.emptyList();
                    }
                }

                // Try generating a new ID
                id = idGenerator.newId();
                int i = 0;
                while (idNameHash.hasKey(id) && i < 5) {
                    id = idGenerator.newId();
                    i++;
                }
                if (idNameHash.hasKey(id)) {
                    ops.unwatch();
                    throw new RuntimeException("Failed to generate a unique ID.");
                }

                // Save the ID
                ops.multi();
                nameIdHash.put(name, id);
                idNameHash.put(id, name);
                List<Object> results = ops.exec();
                return results;
            }
        };

        return redisTemplate.execute(callback);
    }

    // 6-char ID has a space of 56,800,235,584 keys
    private final RandomIdGenerator idGenerator = new RandomIdGenerator(6);

    @Resource
    private StringRedisTemplate redisTemplate;
}
