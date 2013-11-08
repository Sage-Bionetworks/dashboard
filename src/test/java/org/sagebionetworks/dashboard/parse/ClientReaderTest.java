package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ClientReaderTest {
    @Test
    public void test() {
        RepoRecord record = new RepoRecord();
        RecordReader<String> reader = new ClientReader();
        assertEquals("null-client", reader.read(record));
        record.setUserAgent("PYTHON client 1.0");
        assertEquals("python client 1.0", reader.read(record));
    }
}
