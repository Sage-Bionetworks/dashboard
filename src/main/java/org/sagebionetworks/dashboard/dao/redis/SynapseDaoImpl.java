package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.Key.SEPARATOR;
import static org.sagebionetworks.dashboard.dao.redis.Key.SYNAPSE_ENTITY_ID_NAME;
import static org.sagebionetworks.dashboard.dao.redis.Key.SYNAPSE_SESSION;
import static org.sagebionetworks.dashboard.dao.redis.Key.SYNAPSE_USER_ID_NAME;
import static org.sagebionetworks.dashboard.dao.redis.Key.SYNAPSE_USER_EMAIL_ID;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.sagebionetworks.dashboard.http.client.SynapseClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository("synapseDao")
public class SynapseDaoImpl implements SynapseDao {

    public SynapseDaoImpl() {
        synapseClient = new SynapseClient();
    }

    public Long getPrincipalId(final String email) {
        final String key = SYNAPSE_USER_EMAIL_ID + SEPARATOR + email;
        String idStr = valueOps.get(key);
        if (idStr != null) {
            return Long.parseLong(idStr);
        }
        String session = getSession();
        Long id = synapseClient.getPrincipalId(email, session);
        if (id != null) {
            valueOps.set(key, id.toString(), EXPIRE_HOURS, TimeUnit.HOURS);
        }
        return id;
    }

    @Override
    public String getUserDisplayName(final String userId) {
        final String key = SYNAPSE_USER_ID_NAME + SEPARATOR + userId;
        String name = valueOps.get(key);
        if (name == null) {
            String session = getSession();
            name = synapseClient.getDisplayName(userId, session);
            if (name != null) {
                valueOps.set(key, name, EXPIRE_HOURS, TimeUnit.HOURS);
            }
        }
        return name;
    }

    @Override
    public String getEntityName(final String entityId) {
        final String key = SYNAPSE_ENTITY_ID_NAME + SEPARATOR + entityId;
        String name = valueOps.get(key);
        if (name == null) {
            String session = getSession();
            name = synapseClient.getEntityName(entityId, session);
            if (name != null) {
                valueOps.set(key, name, EXPIRE_HOURS, TimeUnit.HOURS);
            }
        }
        return name;
    }

    private String getSession() {
        String session = valueOps.get(SYNAPSE_SESSION);
        if (session != null) {
            return session;
        }
        session = login();
        return session;
    }

    private String login() {
        String session = synapseClient.login();
        if (session != null) {
            valueOps.set(SYNAPSE_SESSION, session, EXPIRE_HOURS, TimeUnit.HOURS);
            return session;
        }
        throw new RuntimeException("Failed to log in to Synapse.");
    }

    private static final long EXPIRE_HOURS = 12;

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    private final SynapseClient synapseClient;
}
