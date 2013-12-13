package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UriChangePasswordFilterTest {
    @Test
    public void test() {
        RecordFilter filter = new UriChangePasswordFilter();
        RepoRecord record = new RepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/user/password");
        assertFalse(filter.matches(record));
        record.setUri("/auth/v1/user/password");
        assertFalse(filter.matches(record));
        record.setUri("/auth/v1/userPasswordEmail");
        assertFalse(filter.matches(record));
        record.setUri("/auth/v1/userPassword");
        assertTrue(filter.matches(record));
    }
}
