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
        InputStream source = ClassLoader.getSystemResourceAsStream("/META-INF/spring/AccessRecordTable.sql");
        String query = null;
        try {
            query = IOUtils.toString(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info(query);
        boolean inited = dwTemplate.execute(query,//"SELECT * FROM information_schema.tables;",
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
