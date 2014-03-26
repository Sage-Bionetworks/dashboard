package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.UserIdReader;

public class ActiveUserMetricTest {
    @Test
    public void test() {
        DayCountMetric metric = new ActiveUserMetric();
        assertEquals("activeUserMetric", metric.getName());
        assertTrue(metric.getRecordReader() instanceof UserIdReader);
        assertEquals(1, metric.getFilters().size());
        assertTrue(metric.getFilters().get(0) instanceof ProdFilter);
    }
}
