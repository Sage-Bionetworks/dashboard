package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ErrorFilterTest {

    @Test
    public void test() {

        RecordFilter<AccessRecord> filter = new ErrorFilter();

        // When the status is null or empty
        RepoRecord record = new RepoRecord();
        assertFalse(filter.matches(record));
        record.setStatus("");
        assertFalse(filter.matches(record));

        record.setStatus("400");
        assertTrue(filter.matches(record));
        record.setStatus("403");
        assertTrue(filter.matches(record));
        record.setStatus("404");
        assertTrue(filter.matches(record));
        record.setStatus("500");
        assertTrue(filter.matches(record));
        record.setStatus("503");
        assertTrue(filter.matches(record));

        record.setStatus("200");
        assertFalse(filter.matches(record));
        record.setStatus("301");
        assertFalse(filter.matches(record));
        record.setStatus("302");
        assertFalse(filter.matches(record));

        record.setStatus("true");
        assertFalse(filter.matches(record));
        record.setStatus("false");
        assertFalse(filter.matches(record));
        record.setStatus("OK");
        assertFalse(filter.matches(record));
    }
}
