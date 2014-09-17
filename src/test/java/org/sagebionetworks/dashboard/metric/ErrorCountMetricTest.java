package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.parse.ErrorFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.RepoRecord;

public class ErrorCountMetricTest {
    @Test
    public void test() {
        SimpleCountMetric metric = new ErrorCountMetric();
        assertEquals("errorCountMetric", metric.getName());
        RecordReader<AccessRecord, String> reader = metric.getRecordReader();
        AccessRecord record = new RepoRecord();
        assertEquals("", reader.read(record));
        assertEquals(3, metric.getFilters().size());
        assertTrue(metric.getFilters().get(0) instanceof ProdFilter);
        assertTrue(metric.getFilters().get(1) instanceof ErrorFilter);
    }
}
