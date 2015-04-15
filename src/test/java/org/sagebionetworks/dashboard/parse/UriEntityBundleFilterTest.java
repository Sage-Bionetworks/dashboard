package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class UriEntityBundleFilterTest {

    @Test
    public void test() {

        RecordFilter<AccessRecord> filter = new UriEntityBundleFilter();

        SynapseRepoRecord record = new SynapseRepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/descendants");
        assertFalse(filter.matches(record));

        record.setUri("/repo/v1/entity/syn2203318/bundle");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/syn1687600/version/1/bundle");
        assertTrue(filter.matches(record));
    }
}
