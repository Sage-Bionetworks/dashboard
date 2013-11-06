package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ErrorFilterTest {

    @Test
    public void test() {

        RecordFilter filter = new ErrorFilter();

        // When the status is null or empty
        RepoRecord record = new RepoRecord();
        assertFalse(filter.matches(record));
        record.setStatus("");
        assertFalse(filter.matches(record));

        record.setStatus("false");
        assertTrue(filter.matches(record));
        record.setStatus("False");
        assertTrue(filter.matches(record));
        record.setStatus("FALSE");
        assertTrue(filter.matches(record));

        record.setStatus("true");
        assertFalse(filter.matches(record));
        record.setStatus("True");
        assertFalse(filter.matches(record));
        record.setStatus("TRUE");
        assertFalse(filter.matches(record));

        record.setStatus("200");
        assertFalse(filter.matches(record));
        record.setStatus("Ok");
        assertFalse(filter.matches(record));
        record.setStatus("OK");
        assertFalse(filter.matches(record));

        // Not implemented yet
        record.setStatus("500");
        assertFalse(filter.matches(record));
        record.setStatus("404");
        assertFalse(filter.matches(record));
    }
}
