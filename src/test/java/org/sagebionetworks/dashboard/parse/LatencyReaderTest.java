package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class LatencyReaderTest {
    @Test
    public void test() {
        SynapseRepoRecord record = new SynapseRepoRecord();
        RecordReader<AccessRecord, Long> reader = new LatencyReader();
        assertNull(reader.read(record));
        record.setLatency(123L);
        assertEquals(123L, reader.read(record).longValue());
    }
}
