package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class ProdFilterTest {

    @Test
    public void test() {

        RecordFilter<AccessRecord> filter = new ProdFilter();

        SynapseRepoRecord record = new SynapseRepoRecord();
        assertFalse(filter.matches(record));
        record.setStack("");
        assertFalse(filter.matches(record));
        record.setStack("staging");
        assertFalse(filter.matches(record));
        record.setStack("dev");
        assertFalse(filter.matches(record));
        record.setStack("Development");
        assertFalse(filter.matches(record));
        record.setStack("whatever");
        assertFalse(filter.matches(record));

        record.setStack("prod");
        assertTrue(filter.matches(record));
        record.setStack("PROD");
        assertTrue(filter.matches(record));
        record.setStack("Prod");
        assertTrue(filter.matches(record));
    }
}
