package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.ErrorFilter;
import org.sagebionetworks.dashboard.parse.MethodUriReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;

public class ErrorByMethodMetricTest {
    @Test
    public void test() {
        UniqueCountMetric<AccessRecord, String> metric = new ErrorByMethodMetric();
        assertEquals("errorByMethodMetric", metric.getName());
        assertTrue(metric.getRecordReader() instanceof MethodUriReader);
        assertEquals(3, metric.getFilters().size());
        assertTrue(metric.getFilters().get(0) instanceof ProdFilter);
        assertTrue(metric.getFilters().get(1) instanceof ErrorFilter);
    }
}
