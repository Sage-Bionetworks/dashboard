package org.sagebionetworks.dashboard.service;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.InOrder;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.test.util.ReflectionTestUtils;

public class AbstractMetricWriterTest {

    public static class StubMetricWriter extends AbstractMetricWriter<AccessRecord, String> {
        @Override
        void write(String metricId, String additionalKey, DateTime timestamp,
                String id) {}
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test() {

        // Set up mocks
        AccessRecord record = mock(AccessRecord.class);

        Metric<AccessRecord, String> metric = mock(Metric.class);
        when(metric.getName()).thenReturn("metric");
        RecordReader<AccessRecord, String> reader = mock(RecordReader.class);
        when(reader.read(record)).thenReturn("value");
        when(metric.getRecordReader()).thenReturn(reader);
        List<RecordFilter<AccessRecord>> filters = new ArrayList<>();
        RecordFilter<AccessRecord> filter1 = mock(RecordFilter.class);
        when(filter1.matches(record)).thenReturn(true);
        filters.add(filter1);
        RecordFilter<AccessRecord> filter2 = mock(RecordFilter.class);
        when(filter2.matches(record)).thenReturn(true);
        filters.add(filter2);
        when(metric.getFilters()).thenReturn(filters);

        List<RecordFilter<AccessRecord>> additionalFilters = new ArrayList<>();
        RecordFilter<AccessRecord> filter3 = mock(RecordFilter.class);
        when(filter3.matches(record)).thenReturn(true);
        additionalFilters.add(filter3);
        RecordFilter<AccessRecord> filter4 = mock(RecordFilter.class);
        when(filter4.matches(record)).thenReturn(true);
        additionalFilters.add(filter4);

        NameIdDao nameIdDao = mock(NameIdDao.class);
        when(nameIdDao.getId(metric.getName())).thenReturn("abcde");

        AbstractMetricWriter<AccessRecord, String> writer = new StubMetricWriter();
        ReflectionTestUtils.setField(writer, "nameIdDao", nameIdDao);

        writer.writeMetric(record, metric, additionalFilters, "");

        // Test order of filter calls -- must call in order
        InOrder inorder = inOrder(filter1, filter2, filter3, filter4);
        inorder.verify(filter1).matches(record);
        inorder.verify(filter2).matches(record);
        inorder.verify(filter3).matches(record);
        inorder.verify(filter4).matches(record);

        verify(nameIdDao, times(1)).getId("metric");
        verify(reader, times(1)).read(record);
    }
}
