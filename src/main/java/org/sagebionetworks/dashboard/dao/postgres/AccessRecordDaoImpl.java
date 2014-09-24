package org.sagebionetworks.dashboard.dao.postgres;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.AccessRecordDao;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.EntityIdReader;
import org.sagebionetworks.dashboard.parse.UserIdReader;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("dwAccessRecordDao")
public class AccessRecordDaoImpl implements AccessRecordDao{

    @Resource
    NamedParameterJdbcTemplate dwTemplate;

    private final String query = "INSERT INTO file_status " + 
            "(object_id, entity_id, elapse_ms, timestamp, host, thread_id, " +
            "user_agent, query, session_id, request_url, user_id, method, " +
            "vm_id, stack, instance, response_status) " +
            "VALUES (:object_id,:entity_id,:elapse_ms,:timestamp,:host,:thread_id," +
            ":user_agent,:query,:session_id,:request_url,:user_id,:method," +
            ":vm_id,:stack,:instance,:response_status);";

    @Override
    public void put(AccessRecord record) {
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("object_id", record.getObjectId());

        String entityId = (new EntityIdReader()).read(record);
        if (entityId.startsWith("syn")) {
            entityId = entityId.substring(3);
        }
        namedParameters.put("entity_id", Integer.parseInt(entityId));

        namedParameters.put("elapse_ms", record.getLatency());
        namedParameters.put("timestamp", record.getTimestamp());
        namedParameters.put("host", record.getHost());
        namedParameters.put("thread_id", Integer.parseInt(record.getThreadId()));
        namedParameters.put("user_agent", record.getUserAgent());
        namedParameters.put("query", record.getQueryString());
        namedParameters.put("session_id", record.getSessionId());
        namedParameters.put("request_url", record.getUri());

        String userId = (new UserIdReader()).read(record);
        if (userId == "null-user-id") {
            namedParameters.put("user_id", null);
        } else {
            namedParameters.put("user_id", Integer.parseInt(userId));
        }

        namedParameters.put("method", record.getMethod());
        namedParameters.put("vm_id", record.getVM());
        namedParameters.put("stack", Integer.parseInt(record.getStack()));
        namedParameters.put("instance", record.getInstance());
        namedParameters.put("response_status", record.getStatus());

        dwTemplate.update(query, namedParameters);
    }

}
