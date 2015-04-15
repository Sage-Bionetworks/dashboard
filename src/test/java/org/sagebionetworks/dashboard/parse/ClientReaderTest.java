package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class ClientReaderTest {
    @Test
    public void test() {
        SynapseRepoRecord record = new SynapseRepoRecord();
        RecordReader<AccessRecord, String> reader = new ClientReader();
        assertEquals("null-client", reader.read(record));
        record.setUserAgent("PYTHON client 1.0");
        assertEquals("python client 1.0", reader.read(record));
    }
}
