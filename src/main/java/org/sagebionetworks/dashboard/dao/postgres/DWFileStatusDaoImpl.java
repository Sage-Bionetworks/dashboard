package org.sagebionetworks.dashboard.dao.postgres;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.FileStatusDao;
import org.sagebionetworks.dashboard.model.FileFailure;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("dwFileStatusDao")
public class DWFileStatusDaoImpl implements FileStatusDao{

    @Resource
    NamedParameterJdbcTemplate dwTemplate;

    private static final String query = "INSERT INTO file_status (file_path, status) VALUES (:file_path,:status);";

    @Override
    public boolean isCompleted(String file) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setCompleted(String file) {
        Map<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("file_path", file);
        namedParameters.put("status", true);
        dwTemplate.update(query, namedParameters);
    }

    @Override
    public boolean isFailed(String file) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setFailed(String file, int lineNumber) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<FileFailure> getFailures() {
        // TODO Auto-generated method stub
        return null;
    }

}
