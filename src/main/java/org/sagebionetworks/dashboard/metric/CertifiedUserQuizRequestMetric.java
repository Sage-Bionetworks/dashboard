package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.parse.MethodFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.TimeFilter;
import org.sagebionetworks.dashboard.parse.UriCuqRequestFilter;
import org.sagebionetworks.dashboard.parse.UserIdFilter;
import org.sagebionetworks.dashboard.parse.UserIdReader;
import org.sagebionetworks.dashboard.service.UniqueCountWriter;
import org.springframework.stereotype.Component;

@Component("certifiedUserQuizRequestMetric")
public class CertifiedUserQuizRequestMetric extends UniqueCountMetric<AccessRecord, String> {

    private RecordReader<AccessRecord, String> reader = new UserIdReader();

    private final List<RecordFilter<AccessRecord>> filters = Collections.unmodifiableList(Arrays.asList(
            new MethodFilter("get"), new UriCuqRequestFilter(), new TimeFilter(1403827200000L),
            new UserIdFilter()));

    @Resource
    private UniqueCountWriter<AccessRecord> uniqueCountWriter;

    @Override
    public List<RecordFilter<AccessRecord>> getFilters() {
        return filters ;
    }

    @Override
    public RecordReader<AccessRecord, String> getRecordReader() {
        return reader;
    }

    @Override
    public void write(AccessRecord record) {
        uniqueCountWriter.writeMetric(record, this);
    }
}
