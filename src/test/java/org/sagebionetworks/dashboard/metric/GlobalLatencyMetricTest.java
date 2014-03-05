package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.parse.LatencyReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;

public class GlobalLatencyMetricTest {
    @Test
    public void test() {
        TimeSeriesMetric metric = new GlobalLatencyMetric();
        assertEquals("globalLatencyMetric", metric.getName());
        assertTrue(metric.getRecordReader() instanceof LatencyReader);
        assertEquals(1, metric.getFilters().size());
        assertTrue(metric.getFilters().get(0) instanceof ProdFilter);
    }
}
