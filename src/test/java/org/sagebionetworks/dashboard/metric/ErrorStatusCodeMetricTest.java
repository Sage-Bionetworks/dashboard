package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.parse.ErrorFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.StatusCodeReader;

public class ErrorStatusCodeMetricTest {
    @Test
    public void test() {
        UniqueCountMetric metric = new ErrorStatusCodeMetric();
        assertEquals("errorStatusCodeMetric", metric.getName());
        assertTrue(metric.getRecordReader() instanceof StatusCodeReader);
        assertEquals(3, metric.getFilters().size());
        assertTrue(metric.getFilters().get(0) instanceof ProdFilter);
        assertTrue(metric.getFilters().get(1) instanceof ErrorFilter);
    }
}
