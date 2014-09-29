package org.sagebionetworks.dashboard.dao.postgres;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.amazonaws.util.IOUtils;

public class DataWarehouseInit {

    private final Logger logger = LoggerFactory.getLogger(DataWarehouseInit.class);

    public DataWarehouseInit(NamedParameterJdbcTemplate dwTemplate) {

        createTable(dwTemplate, "/META-INF/spring/AccessRecordTable.sql");
        createTable(dwTemplate, "/META-INF/spring/FileStatusTable.sql");
        createTable(dwTemplate, "/META-INF/spring/RecordStatusTable.sql");

        logger.info("Data warehouse initialzied.");
    }

    private void createTable(NamedParameterJdbcTemplate dwTemplate, String path) {
        InputStream source = this.getClass().getResourceAsStream(path);

        if (source == null) {
            logger.info("Failed to read the source from " + path
                    + ". Data warehouse initiation failure.");
            return;
        }

        String query = null;
        try {
            query = IOUtils.toString(source);
        } catch (IOException e) {
            logger.info("Failed to convert InputStream into String."
                    + " Data warehouse initiation failure.");
            return;
        }

        dwTemplate.execute(query, new PreparedStatementCallback<Boolean>() {
            @Override
            public Boolean doInPreparedStatement(PreparedStatement ps)
                    throws SQLException, DataAccessException {
                return ps.execute();
            }});
    }
}
