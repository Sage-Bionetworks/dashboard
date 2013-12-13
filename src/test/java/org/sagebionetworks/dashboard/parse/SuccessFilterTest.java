package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SuccessFilterTest {
    @Test
    public void test() {

        RecordFilter filter = new SuccessFilter();

        RepoRecord record = new RepoRecord();
        assertFalse(filter.matches(record));
        record.setStatus("");
        assertFalse(filter.matches(record));

        record.setStatus("false");
        assertFalse(filter.matches(record));
        record.setStatus("False");
        assertFalse(filter.matches(record));
        record.setStatus("FALSE");
        assertFalse(filter.matches(record));

        record.setStatus("true");
        assertTrue(filter.matches(record));
        record.setStatus("True");
        assertTrue(filter.matches(record));
        record.setStatus("TRUE");
        assertTrue(filter.matches(record));

        // Not implemented yet
        record.setStatus("200");
        assertFalse(filter.matches(record));
        record.setStatus("Ok");
        assertFalse(filter.matches(record));
        record.setStatus("OK");
        assertFalse(filter.matches(record));

        record.setStatus("500");
        assertFalse(filter.matches(record));
        record.setStatus("404");
        assertFalse(filter.matches(record));
    }
}
