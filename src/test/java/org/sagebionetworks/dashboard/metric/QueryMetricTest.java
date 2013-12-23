package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.parse.LatencyReader;
import org.sagebionetworks.dashboard.parse.MethodFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.UriQueryFilter;

public class QueryMetricTest {
    @Test
    public void test() {
        TimeSeriesMetric metric = new QueryMetric();
        assertEquals("queryMetric", metric.getName());
        assertTrue(metric.getRecordReader() instanceof LatencyReader);
        assertEquals(3, metric.getFilters().size());
        assertTrue(metric.getFilters().get(0) instanceof ProdFilter);
        assertTrue(metric.getFilters().get(1) instanceof MethodFilter);
        assertTrue(metric.getFilters().get(2) instanceof UriQueryFilter);
    }
}
