package org.sagebionetworks.dashboard.dao.postgres;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class DataWarehouseInit {

    private final Logger logger = LoggerFactory.getLogger(DataWarehouseInit.class);

    public DataWarehouseInit(NamedParameterJdbcTemplate dwTemplate) {
        boolean inited = dwTemplate.execute("SELECT * FROM information_schema.tables;",
                new PreparedStatementCallback<Boolean>() {
            @Override
            public Boolean doInPreparedStatement(PreparedStatement ps)
                    throws SQLException, DataAccessException {
                return ps.execute();
            }});
        if (!inited) {
            throw new RuntimeException("Data warehouse initiation failure.");
        }
        logger.info("Data warehouse initialzied.");
    }
}
