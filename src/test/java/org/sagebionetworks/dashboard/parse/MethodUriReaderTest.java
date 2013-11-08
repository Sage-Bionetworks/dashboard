package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MethodUriReaderTest {
    @Test
    public void test() {
        RepoRecord record = new RepoRecord();
        RecordReader<String> reader = new MethodUriReader();
        assertEquals("null-method null-uri", reader.read(record));
        record.setUri("/REPO/v1/SYN12345");
        assertEquals("null-method /repo/v[num]/syn[num]", reader.read(record));
        record.setMethod("GET");
        assertEquals("get /repo/v[num]/syn[num]", reader.read(record));
    }
}
