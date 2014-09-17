package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserIdReaderTest {
    @Test
    public void test() {
        RepoRecord record = new RepoRecord();
        RecordReader<AccessRecord, String> reader = new UserIdReader();
        assertEquals("null-user-id", reader.read(record));
        record.setUserId("123");
        assertEquals("123", reader.read(record));
    }
}
