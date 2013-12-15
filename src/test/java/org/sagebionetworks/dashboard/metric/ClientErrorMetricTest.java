package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.metric.ClientErrorMetric;
import org.sagebionetworks.dashboard.metric.UniqueCountToWrite;
import org.sagebionetworks.dashboard.parse.ClientReader;
import org.sagebionetworks.dashboard.parse.ErrorFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;

public class ClientErrorMetricTest {
    @Test
    public void test() {
        UniqueCountToWrite metricToWrite = new ClientErrorMetric();
        assertEquals("clientErrorMetric", metricToWrite.getName());
        assertTrue(metricToWrite.getRecordReader() instanceof ClientReader);
        assertEquals(2, metricToWrite.getFilters().size());
        assertTrue(metricToWrite.getFilters().get(0) instanceof ProdFilter);
        assertTrue(metricToWrite.getFilters().get(1) instanceof ErrorFilter);
    }
}
