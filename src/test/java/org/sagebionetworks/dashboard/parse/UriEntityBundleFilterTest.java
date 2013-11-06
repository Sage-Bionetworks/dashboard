package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UriEntityBundleFilterTest {

    @Test
    public void test() {

        RecordFilter filter = new UriEntityBundleFilter();

        RepoRecord record = new RepoRecord();
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
