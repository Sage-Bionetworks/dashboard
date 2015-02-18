package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.*;

import org.junit.Test;

public class UriQueryTableFilterTest {

    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UriQueryTableFilter();
        RepoRecord record = new RepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/download/");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/query/");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/getRows");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/query/async/start");
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/query/nextPage/async/start");
        assertTrue(filter.matches(record));
    }

}
