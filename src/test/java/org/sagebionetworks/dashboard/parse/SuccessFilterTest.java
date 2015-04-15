package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class SuccessFilterTest {
    @Test
    public void test() {

        RecordFilter<AccessRecord> filter = new SuccessFilter();

        SynapseRepoRecord record = new SynapseRepoRecord();
        assertFalse(filter.matches(record));
        record.setStatus("");
        assertFalse(filter.matches(record));

        record.setStatus("400");
        assertFalse(filter.matches(record));
        record.setStatus("404");
        assertFalse(filter.matches(record));
        record.setStatus("500");
        assertFalse(filter.matches(record));

        record.setStatus("200");
        assertTrue(filter.matches(record));
        record.setStatus("301");
        assertTrue(filter.matches(record));
        record.setStatus("303");
        assertTrue(filter.matches(record));

        record.setStatus("Ok");
        assertFalse(filter.matches(record));
        record.setStatus("True");
        assertFalse(filter.matches(record));
        record.setStatus("False");
        assertFalse(filter.matches(record));
    }
}
