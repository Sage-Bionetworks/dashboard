package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.model.redis.RedisKey.ID_NAME;
import static org.sagebionetworks.dashboard.model.redis.RedisKey.NAME_ID;

import java.util.ArrayList;
import java.util.Collection;

import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.util.RandomIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

@Repository
public class NameIdDaoImpl implements NameIdDao {

    private final RandomIdGenerator idGenerator = new RandomIdGenerator();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public String getId(final String name) {
        BoundHashOperations<String, String, String> nameIdHash = getNameIdHash();
        String id = nameIdHash.get(name);
        if (id == null) {
            generateId(name);
            id = nameIdHash.get(name);
        }
        return id;
    }

    @Override
    public String getName(final String id) {
        BoundHashOperations<String, String, String> idNameHash = getIdNameHash();
        return idNameHash.get(id);
    }

    /**
     * Generates a new ID for the name and updates the mappings.
     */
    private void generateId(final String name) {

        redisTemplate.execute(new SessionCallback<String>() {
            @Override
            public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {

                // Get the operations
                @SuppressWarnings("unchecked")
                RedisOperations<String, String> ops = (RedisOperations<String, String>) operations;
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
                    ops.unwatch();
                    return id;
                }

                // Try generating a new ID
                id = idGenerator.newId();
                int i = 0;
                while (idNameHash.hasKey(id) && i < 3) {
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
                ops.exec();
                return id;
            }});
    }

    private BoundHashOperations<String, String, String> getNameIdHash() {
        return redisTemplate.boundHashOps(NAME_ID);
    }

    private BoundHashOperations<String, String, String> getIdNameHash() {
        return redisTemplate.boundHashOps(ID_NAME);
    }
}
