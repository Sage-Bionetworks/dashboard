package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.*;

import org.junit.Test;

public class UriUploadTableFilterTest {

    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UriUploadTableFilter();
        RepoRecord record = new RepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/download/");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/query/");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/getRows");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/query/async/start");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/query/nextPage/async/start");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/append/async/start");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/deleteRows");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/upload/csv/preview/async/start");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/upload/csv/async/start");
        assertTrue(filter.matches(record));
    }

}
