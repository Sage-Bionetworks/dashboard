package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ProdFilterTest {

    @Test
    public void test() {

        RecordFilter<AccessRecord> filter = new ProdFilter();

        RepoRecord record = new RepoRecord();
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
