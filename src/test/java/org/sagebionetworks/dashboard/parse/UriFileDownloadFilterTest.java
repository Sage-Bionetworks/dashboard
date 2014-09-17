package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UriFileDownloadFilterTest {
    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UriFileDownloadFilter();
        RepoRecord record = new RepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn123");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/file");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/123/file");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn123/file");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/syn123/version/1/file");
        assertTrue(filter.matches(record));
    }
}
