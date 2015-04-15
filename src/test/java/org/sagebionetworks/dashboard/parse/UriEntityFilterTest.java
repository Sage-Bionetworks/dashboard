package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class UriEntityFilterTest {
    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UriEntityFilter();
        SynapseRepoRecord record = new SynapseRepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/descendants");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/bundle");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/");
        assertFalse(filter.matches(record));
    }
}
