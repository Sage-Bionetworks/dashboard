package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UriGetDescendantsFilterTest {
    @Test
    public void test() {
        RecordFilter filter = new UriGetDescendantsFilter();
        RepoRecord record = new RepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn123");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/descendants");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn123/descendants");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/syn123/descendants/2");
        assertTrue(filter.matches(record));
    }
}
