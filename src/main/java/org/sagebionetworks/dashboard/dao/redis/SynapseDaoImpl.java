package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.Key.SEPARATOR;
import static org.sagebionetworks.dashboard.dao.redis.Key.SYNAPSE_ENTITY_ID_NAME;
import static org.sagebionetworks.dashboard.dao.redis.Key.SYNAPSE_SESSION;
import static org.sagebionetworks.dashboard.dao.redis.Key.SYNAPSE_USER_ID_NAME;
import static org.sagebionetworks.dashboard.dao.redis.Key.SYNAPSE_USER_ID_EMAIL;
import static org.sagebionetworks.dashboard.dao.redis.Key.SYNAPSE_USER_EMAIL_ID;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.sagebionetworks.dashboard.http.client.SynapseClient;
import org.sagebionetworks.dashboard.http.client.SynapseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository("synapseDao")
public class SynapseDaoImpl implements SynapseDao {

    @Autowired
    public SynapseDaoImpl(SynapseClient synapseClient) {
        // Get the team of dashboard users
        this.synapseClient = synapseClient;
        final String session = synapseClient.login();
        dashboardTeamId = synapseClient.getTeamId(TEAM_NAME, session);
        if (dashboardTeamId == null) {
            throw new RuntimeException("Cannot find the team for " + TEAM_NAME);
        }
    }

    @Override
    public String getUserName(final String userId) {
        String name = hashOps.get(SYNAPSE_USER_ID_NAME, userId);
        if (name == null) {
            String session = getSession();
            name = synapseClient.getUserName(userId, session);
            if (name != null) {
                hashOps.put(SYNAPSE_USER_ID_NAME, userId, name);
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

    @Override
    public void refreshUsers() {
        final String session = getSession();
        long offset = 0L;
        final long limit = 200L;
        List<SynapseUser> users = synapseClient.getUsers(offset, limit, session);
        while (users.size() > 0) {
            for (SynapseUser user : users) {
                final String userId = user.getUserId();
                final String email = user.getEmail();
                if (email != null && !email.isEmpty()) {
                    hashOps.put(SYNAPSE_USER_ID_EMAIL, userId, email);
                    hashOps.put(SYNAPSE_USER_EMAIL_ID, email, userId);
                }
                final String userName = user.getUserName();
                if (userName != null && !userName.isEmpty()) {
                    hashOps.put(SYNAPSE_USER_ID_NAME, userId, userName);
                }
            }
            offset = offset + limit;
            users = synapseClient.getUsers(offset, limit, session);
        }
        redisTemplate.expire(SYNAPSE_USER_ID_EMAIL, EXPIRE_HOURS, TimeUnit.HOURS);
        redisTemplate.expire(SYNAPSE_USER_EMAIL_ID, EXPIRE_HOURS, TimeUnit.HOURS);
        redisTemplate.expire(SYNAPSE_USER_ID_NAME, EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public List<String> getUserNames(List<String> userIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getEntityNames(List<String> entityIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getProjects(List<String> entityIds) {
        // TODO Auto-generated method stub
        return null;
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

    // Synapse team name for the list of dashboard users
    private static final String TEAM_NAME = "SageBioEmployees"; 

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOps;

    @Resource(name="redisTemplate")
    private HashOperations<String, String, String> hashOps;

    private final SynapseClient synapseClient;

    private final Long dashboardTeamId;
}
