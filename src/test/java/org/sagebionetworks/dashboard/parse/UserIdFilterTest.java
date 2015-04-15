package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class UserIdFilterTest {
    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UserIdFilter();

        SynapseRepoRecord record = new SynapseRepoRecord();
        assertTrue(filter.matches(record));
        record.setUserId("3319059");
        assertFalse(filter.matches(record));
        record.setUserId("");
        assertTrue(filter.matches(record));
    }
}
