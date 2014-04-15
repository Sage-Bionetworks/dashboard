package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.parse.ClientReader;
import org.sagebionetworks.dashboard.parse.ErrorFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;

public class ErrorByClientMetricTest {
    @Test
    public void test() {
        UniqueCountMetric metric = new ErrorByClientMetric();
        assertEquals("errorByClientMetric", metric.getName());
        assertTrue(metric.getRecordReader() instanceof ClientReader);
        assertEquals(2, metric.getFilters().size());
        assertTrue(metric.getFilters().get(0) instanceof ProdFilter);
        assertTrue(metric.getFilters().get(1) instanceof ErrorFilter);
    }
}
