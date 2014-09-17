package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.StatusCodeReader;

public class StatusCodeMetricTest {
    @Test
    public void test() {
        UniqueCountMetric<AccessRecord, String> metric = new StatusCodeMetric();
        assertEquals("statusCodeMetric", metric.getName());
        assertTrue(metric.getRecordReader() instanceof StatusCodeReader);
        assertEquals(2, metric.getFilters().size());
        assertTrue(metric.getFilters().get(0) instanceof ProdFilter);
    }
}
