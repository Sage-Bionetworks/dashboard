package org.sagebionetworks.dashboard.dao.redis;

import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.model.redis.RedisKey;
import org.sagebionetworks.dashboard.util.RandomIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NameIdDaoImpl implements NameIdDao {

    private final RandomIdGenerator idGenerator = new RandomIdGenerator();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public String getId(String name, String nameIdKey, String idNameKey) {

        final BoundSetOperations<String, String> idSet = redisTemplate.boundSetOps(RedisKey.ID);
        final BoundHashOperations<String, String, String> nameIdHash = redisTemplate.boundHashOps(nameIdKey);
        final BoundHashOperations<String, String, String> idNameHash =redisTemplate.boundHashOps(idNameKey);

        String id = nameIdHash.get(name);
        if (id == null) {

            // Generate a new ID
            id = idGenerator.newId();
            Long result = idSet.add(id);
            // Retry at most 3 times in case of duplicate keys
            int i = 0;
            while (result == 0 && i < 3) {
                id = idGenerator.newId();
                result = idSet.add(RedisKey.ID, id);
                i++;
            }
            if (result.longValue() == 0) {
                throw new RuntimeException("Failed to generate a new ID.");
            }

            // Add the new ID to the mappings
            boolean success = nameIdHash.putIfAbsent(name, id);
            if (success) {
                idNameHash.put(id, name);
            } else {
                // In case some other thread gets ahead of me
                id = nameIdHash.get(name);
                if (id == null) {
                    throw new RuntimeException("Failed to insert the new ID.");
                }
            }
        }

        return id;
    }

    @Override
    public String getName(String id, String idNameKey) {
        final BoundHashOperations<String, String, String> idNameHash =redisTemplate.boundHashOps(idNameKey);
        return idNameHash.get(id);
    }
}
