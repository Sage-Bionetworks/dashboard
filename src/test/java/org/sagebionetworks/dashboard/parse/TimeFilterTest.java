package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class TimeFilterTest {

    @Test
    public void test() {
        TimeFilter filter = new TimeFilter(1403827200000L);
        SynapseRepoRecord record = new SynapseRepoRecord();
        record.setTimestamp(1403827100000L);
        assertFalse(filter.matches(record));
        record.setTimestamp(1403827200000L);
        assertTrue(filter.matches(record));
        record.setTimestamp(1403827200001L);
        assertTrue(filter.matches(record));
    }
}
