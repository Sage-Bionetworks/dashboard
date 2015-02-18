package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.*;

import org.junit.Test;

public class UriDownloadTableFilterTest {

    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UriDownloadTableFilter();
        RepoRecord record = new RepoRecord();
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
