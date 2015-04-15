package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.parse.ClientSummaryReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;

public class TopClientMetricTest {
    @Test
    public void test() {
        UniqueCountMetric<AccessRecord, String> metric = new TopClientMetric();
        assertEquals("topClientMetric", metric.getName());
        assertTrue(metric.getRecordReader() instanceof ClientSummaryReader);
        assertEquals(2, metric.getFilters().size());
        assertTrue(metric.getFilters().get(0) instanceof ProdFilter);
    }
}
