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
import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.test.util.ReflectionTestUtils;

public class AbstractMetricWriterTest {

    public static class StubMetricWriter extends AbstractMetricWriter<String> {
        @Override
        void write(String metricId, DateTime timestamp, String value) {}
    }

    @Test
    public void test() {

        // Set up mocks
        Record record = mock(Record.class);

        @SuppressWarnings("unchecked")
        MetricToWrite<String> metric = mock(MetricToWrite.class);
        when(metric.getName()).thenReturn("metric");
        @SuppressWarnings("unchecked")
        RecordReader<String> reader = mock(RecordReader.class);
        when(reader.read(record)).thenReturn("value");
        when(metric.getRecordReader()).thenReturn(reader);
        List<RecordFilter> filters = new ArrayList<>();
        RecordFilter filter1 = mock(RecordFilter.class);
        when(filter1.matches(record)).thenReturn(true);
        filters.add(filter1);
        RecordFilter filter2 = mock(RecordFilter.class);
        when(filter2.matches(record)).thenReturn(true);
        filters.add(filter2);
        when(metric.getFilters()).thenReturn(filters);

        List<RecordFilter> additionalFilters = new ArrayList<>();
        RecordFilter filter3 = mock(RecordFilter.class);
        when(filter3.matches(record)).thenReturn(true);
        additionalFilters.add(filter3);
        RecordFilter filter4 = mock(RecordFilter.class);
        when(filter4.matches(record)).thenReturn(true);
        additionalFilters.add(filter4);

        NameIdDao nameIdDao = mock(NameIdDao.class);
        when(nameIdDao.getId(metric.getName())).thenReturn("abcde");

        AbstractMetricWriter<String> writer = new StubMetricWriter();
        ReflectionTestUtils.setField(writer, "nameIdDao", nameIdDao);

        writer.writeMetric(record, metric, additionalFilters);

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
