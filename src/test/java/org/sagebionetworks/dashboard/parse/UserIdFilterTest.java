package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.*;

import org.junit.Test;

public class UserIdFilterTest {
    @Test
    public void test() {
        RecordFilter filter = new UserIdFilter();

        RepoRecord record = new RepoRecord();
        assertTrue(filter.matches(record));
        record.setUserId("3319059");
        assertFalse(filter.matches(record));
        record.setUserId("");
        assertTrue(filter.matches(record));
    }
}
