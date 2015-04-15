package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class UriQueryFilterTest {
    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UriQueryFilter();
        SynapseRepoRecord record = new SynapseRepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/query?");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/query");
        assertTrue(filter.matches(record));
    }
}
