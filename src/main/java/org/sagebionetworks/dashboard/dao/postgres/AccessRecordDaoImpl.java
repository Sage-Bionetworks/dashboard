package org.sagebionetworks.dashboard.dao.postgres;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.AccessRecordDao;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.EntityIdReader;
import org.sagebionetworks.dashboard.parse.UserIdReader;
import org.sagebionetworks.dashboard.service.RepoRecordWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("dwAccessRecordDao")
public class AccessRecordDaoImpl implements AccessRecordDao{

    private final Logger logger = LoggerFactory.getLogger(RepoRecordWorker.class);

    @Resource
    NamedParameterJdbcTemplate dwTemplate;

    private static final String insertRecord = "INSERT INTO access_record " + 
            "(object_id, entity_id, elapse_ms, timestamp, host, thread_id, " +
            "user_agent, query, session_id, request_url, user_id, method, " +
            "vm_id, stack, instance, response_status) " +
            "VALUES (:object_id,:entity_id,:elapse_ms,:timestamp,:host,:thread_id," +
            ":user_agent,:query,:session_id,:request_url,:user_id,:method," +
            ":vm_id,:stack,:instance,:response_status);";

    private static final String clearTable = "DELETE FROM access_record";

    @Override
    public void put(AccessRecord record) {
        Map<String, Object> namedParameters = getParameters(record);

        try {
            dwTemplate.update(insertRecord, namedParameters);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        dwTemplate.execute(clearTable, new PreparedStatementCallback<Boolean>() {
            @Override
            public Boolean doInPreparedStatement(PreparedStatement ps)
                    throws SQLException, DataAccessException {
                return ps.execute();
            }});
        logger.info("access_record table is clear. ");
    }

    private Map<String, Object> getParameters(AccessRecord record) {
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("object_id", record.getObjectId());

        String entityId = (new EntityIdReader()).read(record);
        // entityId can be null or any arbitrary user input string
        try {
            if (entityId.startsWith("syn")) {
                entityId = entityId.substring(3);
            }
            namedParameters.put("entity_id", Integer.parseInt(entityId));
        } catch (Throwable e) {
            namedParameters.put("entity_id", null);
        }

        namedParameters.put("elapse_ms", record.getLatency());
        namedParameters.put("timestamp", record.getTimestamp());
        namedParameters.put("host", record.getHost());
        namedParameters.put("thread_id", Long.parseLong(record.getThreadId()));
        namedParameters.put("user_agent", record.getUserAgent());
        namedParameters.put("query", record.getQueryString());
        namedParameters.put("session_id", record.getSessionId());
        namedParameters.put("request_url", record.getUri());

        String userId = (new UserIdReader()).read(record);
        try {
            namedParameters.put("user_id", Integer.parseInt(userId));
        } catch (Throwable e) {
            namedParameters.put("user_id", null);
        }

        namedParameters.put("method", record.getMethod());
        namedParameters.put("vm_id", record.getVM());
        namedParameters.put("stack", record.getStack());
        namedParameters.put("instance", Integer.parseInt(record.getInstance()));
        namedParameters.put("response_status", Integer.parseInt(record.getStatus()));
        return namedParameters;
    }
}
