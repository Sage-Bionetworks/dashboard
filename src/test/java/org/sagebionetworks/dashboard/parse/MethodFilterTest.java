package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class MethodFilterTest {

    @Test
    public void test() {

        RecordFilter<AccessRecord> filter = new MethodFilter("POST");

        SynapseRepoRecord record = new SynapseRepoRecord();
        assertFalse(filter.matches(record));
        record.setMethod("");
        assertFalse(filter.matches(record));
        record.setMethod("GET");
        assertFalse(filter.matches(record));
        record.setMethod("POST ");
        assertFalse(filter.matches(record));

        record.setMethod("POST");
        assertTrue(filter.matches(record));
        record.setMethod("post");
        assertTrue(filter.matches(record));
        record.setMethod("Post");
        assertTrue(filter.matches(record));
    }
}
