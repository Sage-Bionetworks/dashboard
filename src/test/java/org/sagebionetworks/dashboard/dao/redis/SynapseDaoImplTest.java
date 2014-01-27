package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;
import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.springframework.data.redis.core.RedisTemplate;

public class SynapseDaoImplTest extends AbstractRedisDaoTest {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private SynapseDao synapseDao;

    @Test
    public void test() {

        // Synapse User Guide
        String entityName = synapseDao.getEntityName("syn1669771");
        assertNotNull(entityName);
        assertFalse(entityName.isEmpty());
        entityName = synapseDao.getEntityName("273950");
        assertNull(entityName);

        // User "anonymous"
        String userName = synapseDao.getUserName("273950");
        assertNotNull(userName);
        assertFalse(userName.isEmpty());
        userName = synapseDao.getUserName("syn1669771");
        assertNull(userName);

        // Verify timeout
        String key = Key.SYNAPSE_ENTITY_ID_NAME + ":" + "syn1669771";
        Long expire = redisTemplate.getExpire(key, TimeUnit.HOURS);
        assertNotNull(expire);
        assertTrue(expire.longValue() == 12L || expire.longValue() == 11L);
        key = Key.SYNAPSE_USER_ID_NAME + ":" + "273950";
        expire = redisTemplate.getExpire(key, TimeUnit.HOURS);
        assertNotNull(expire);
        assertTrue(expire.longValue() == 12L || expire.longValue() == 11L);
    }
}
