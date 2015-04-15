package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class UriDownloadTableFilterTest {

    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UriDownloadTableFilter();
        SynapseRepoRecord record = new SynapseRepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/download/");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/download/csv/async/start");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/upload/");
        assertFalse(filter.matches(record));
    }

}
