package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class UserIdReaderTest {
    @Test
    public void test() {
        SynapseRepoRecord record = new SynapseRepoRecord();
        RecordReader<AccessRecord, String> reader = new UserIdReader();
        assertEquals("null-user-id", reader.read(record));
        record.setUserId("123");
        assertEquals("123", reader.read(record));
    }
}
