package org.sagebionetworks.dashboard.dao.postgres;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.FailedRecordDao;
import org.sagebionetworks.dashboard.model.WriteRecordResult;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("dwFailedRecordDao")
public class DWFailedRecordDaoImpl implements FailedRecordDao{

    @Resource
    NamedParameterJdbcTemplate dwTemplate;

    private final String query = "INSERT INTO record_status (file_path, line_number, metric, status) VALUES (:file_path,:line_number,:metric,:status);";

    @Override
    public void put(WriteRecordResult result) {
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("file_path", result.file());
        namedParameters.put("line_number", result.line());
        namedParameters.put("metric", result.metric());
        namedParameters.put("status", result.status());
        dwTemplate.update(query, namedParameters);
    }

}
