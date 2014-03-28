package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class WikiObjectIdReaderTest {
    @Test
    public void test() {
        RecordReader<String> reader = new WikiObjectIdReader();
        RepoRecord record = new RepoRecord();
        assertNull(reader.read(record));
        record.setObjectId("12345");
        assertNull(reader.read(record));
        record.setUri("/repo/v1/entity/syn12345/wiki2");
        assertEquals("syn12345", reader.read(record));
        record.setUri("/repo/v1/evaluation/12345/wiki2");
        assertEquals("12345", reader.read(record));
        record.setUri("/repo/v1/entity/SYN12345/wiki2/6789");
        assertEquals("syn12345", reader.read(record));
    }
}
