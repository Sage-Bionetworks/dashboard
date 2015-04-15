package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class UriWiki2FilterTest {
    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UriWiki2Filter();
        SynapseRepoRecord record = new SynapseRepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/evaluation/2352496");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/descendants");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/evaluation/2352496/wiki2");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2343035/wiki2");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2343035/wiki2/1234");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/evaluation/2352496/wiki2/1234");
        assertTrue(filter.matches(record));
    }
}
