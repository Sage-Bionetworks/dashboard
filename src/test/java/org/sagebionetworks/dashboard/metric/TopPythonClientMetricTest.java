package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.ClientFilter;
import org.sagebionetworks.dashboard.parse.ClientReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;

public class TopPythonClientMetricTest {
    @Test
    public void test() {
        UniqueCountMetric<AccessRecord, String> metric = new TopPythonClientMetric();
        assertEquals("topPythonClientMetric", metric.getName());
        assertTrue(metric.getRecordReader() instanceof ClientReader);
        assertEquals(3, metric.getFilters().size());
        assertTrue(metric.getFilters().get(0) instanceof ProdFilter);
        assertEquals(metric.getFilters().get(1), ClientFilter.PYTHON);
    }
}
