package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.parse.EntityIdReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.UriFileDownloadFilter;

public class FileDownloadMetricTest {
    @Test
    public void test() {
        UniqueCountMetric metric = new FileDownloadMetric();
        assertEquals("fileDownloadMetric", metric.getName());
        assertTrue(metric.getRecordReader() instanceof EntityIdReader);
        assertEquals(3, metric.getFilters().size());
        assertTrue(metric.getFilters().get(0) instanceof ProdFilter);
        assertTrue(metric.getFilters().get(1) instanceof UriFileDownloadFilter);
    }
}
