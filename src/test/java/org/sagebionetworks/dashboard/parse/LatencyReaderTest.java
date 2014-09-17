package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class LatencyReaderTest {
    @Test
    public void test() {
        RepoRecord record = new RepoRecord();
        RecordReader<AccessRecord, Long> reader = new LatencyReader();
        assertNull(reader.read(record));
        record.setLatency(123L);
        assertEquals(123L, reader.read(record).longValue());
    }
}
