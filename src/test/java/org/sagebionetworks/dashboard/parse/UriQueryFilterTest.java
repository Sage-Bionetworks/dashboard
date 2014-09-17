package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UriQueryFilterTest {
    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UriQueryFilter();
        RepoRecord record = new RepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/query?");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/query");
        assertTrue(filter.matches(record));
    }
}
