package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UriSearchFilterTest {
    @Test
    public void test() {
        RecordFilter filter = new UriSearchFilter();
        RepoRecord record = new RepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/search?");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/search");
        assertTrue(filter.matches(record));
    }
}
