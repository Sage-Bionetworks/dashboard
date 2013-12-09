package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class EntityIdReaderTest {
    @Test
    public void test() {
        RecordReader<String> reader = new EntityIdReader();
        RepoRecord record = new RepoRecord();
        assertNull(reader.read(record));
        record.setObjectId("12345");
        assertNull(reader.read(record));
        record.setUri("/repo/v1/entity/user");
        assertNull(reader.read(record));
        record.setUri("/repo/v1/entity/syn12345");
        assertEquals("syn12345", reader.read(record));
        record.setUri("/repo/v1/entity/SYN12345");
        assertEquals("SYN12345", reader.read(record));
        record.setObjectId("syn1");
        assertEquals("syn1", reader.read(record));
    }
}
