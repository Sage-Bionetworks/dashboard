package org.sagebionetworks.dashboard.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.parse.ErrorFilter;
import org.sagebionetworks.dashboard.parse.MethodUriReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;

public class MethodErrorMetricTest {
    @Test
    public void test() {
        UniqueCountToWrite metricToWrite = new MethodErrorMetric();
        assertEquals("methodErrorMetric", metricToWrite.getName());
        assertTrue(metricToWrite.getRecordReader() instanceof MethodUriReader);
        assertEquals(2, metricToWrite.getFilters().size());
        assertTrue(metricToWrite.getFilters().get(0) instanceof ProdFilter);
        assertTrue(metricToWrite.getFilters().get(1) instanceof ErrorFilter);
    }
}
