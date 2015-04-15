package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class UriUpdateTableFilterTest {

    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UriUpdateTableFilter();
        SynapseRepoRecord record = new SynapseRepoRecord();
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
        assertTrue(filter.matches(record));
        record.setUri("/repo/v1/entity/syn2203318/table/deleteRows");
        assertTrue(filter.matches(record));
    }
}
