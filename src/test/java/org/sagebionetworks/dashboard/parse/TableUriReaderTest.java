package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class TableUriReaderTest {

    @Test
    public void test() {
        RecordReader<AccessRecord, String> reader = new TableUriReader();
        SynapseRepoRecord record = new SynapseRepoRecord();
        record.setUri("/repo/v1/entity/syn3163713/table/append/async/start");
        assertEquals("Update", reader.read(record));
        record.setUri("/repo/v1/entity/syn3163713/table/deleteRows");
        assertEquals("Update", reader.read(record));
        record.setUri("/repo/v1/entity/syn3163713/table/getRows");
        assertEquals("Query", reader.read(record));
        record.setUri("/repo/v1/entity/syn3163713/table/query/async/start");
        assertEquals("Query", reader.read(record));
        record.setUri("/repo/v1/entity/syn3163713/table/query/nextPage/async/start");
        assertEquals("Query", reader.read(record));
        record.setUri("/repo/v1/entity/syn3163713/table/query/async/get/29639");
        assertEquals("Query", reader.read(record));
        record.setUri("/repo/v1/entity/syn3163713/table/download/csv/async/start");
        assertEquals("Download", reader.read(record));
        record.setUri("/repo/v1/entity/syn3163713/table/upload/csv/preview/async/start");
        assertEquals("Upload", reader.read(record));
        record.setUri("/repo/v1/entity/syn3163713/table/upload/csv/async/start");
        assertEquals("Upload", reader.read(record));
        record.setUri("/repo/v1/entity/syn3163713/table");
        assertEquals("Uncategorized", reader.read(record));
    }

}
