package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.parse.MethodUriReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;

public class TopMethodMetricTest {
    @Test
    public void test() {
        UniqueCountMetric<AccessRecord, String> metric = new TopMethodMetric();
        assertEquals("topMethodMetric", metric.getName());
        assertTrue(metric.getRecordReader() instanceof MethodUriReader);
        assertEquals(2, metric.getFilters().size());
        assertTrue(metric.getFilters().get(0) instanceof ProdFilter);
    }
}
