package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class UriQueryTableFilterTest {

    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UriQueryTableFilter();
        SynapseRepoRecord record = new SynapseRepoRecord();
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
