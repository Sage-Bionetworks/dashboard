package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class UriCreateUserFilterTest {

    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UriCreateUserFilter();
        SynapseRepoRecord record = new SynapseRepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/userProfile");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/user");
        assertFalse(filter.matches(record));
        record.setUri("/auth/v1/user/password");
        assertFalse(filter.matches(record));
        record.setUri("/auth/v1/user");
        assertTrue(filter.matches(record));
    }
}
