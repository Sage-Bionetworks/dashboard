package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.parse.EntityIdReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;

public class TopEntityMetricTest {
    @Test
    public void test() {
        UniqueCountMetric metric = new TopEntityMetric();
        assertEquals("topEntityMetric", metric.getName());
        assertTrue(metric.getRecordReader() instanceof EntityIdReader);
        assertEquals(1, metric.getFilters().size());
        assertTrue(metric.getFilters().get(0) instanceof ProdFilter);
    }
}
