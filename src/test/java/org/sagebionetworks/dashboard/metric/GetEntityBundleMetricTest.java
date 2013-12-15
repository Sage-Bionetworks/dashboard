package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.metric.GetEntityBundleMetric;
import org.sagebionetworks.dashboard.metric.TimeSeriesToWrite;
import org.sagebionetworks.dashboard.parse.LatencyReader;
import org.sagebionetworks.dashboard.parse.MethodFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.UriEntityBundleFilter;

public class GetEntityBundleMetricTest {
    @Test
    public void test() {
        TimeSeriesToWrite metricToWrite = new GetEntityBundleMetric();
        assertEquals("getEntityBundleMetric", metricToWrite.getName());
        assertTrue(metricToWrite.getRecordReader() instanceof LatencyReader);
        assertEquals(3, metricToWrite.getFilters().size());
        assertTrue(metricToWrite.getFilters().get(0) instanceof ProdFilter);
        assertTrue(metricToWrite.getFilters().get(1) instanceof MethodFilter);
        assertTrue(metricToWrite.getFilters().get(2) instanceof UriEntityBundleFilter);
    }
}
