package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MethodFilterTest {

    @Test
    public void test() {

        RecordFilter<AccessRecord> filter = new MethodFilter("POST");

        RepoRecord record = new RepoRecord();
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
