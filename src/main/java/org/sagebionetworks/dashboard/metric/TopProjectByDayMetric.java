package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UserIdFilter;
import org.sagebionetworks.dashboard.service.DayCountWriter;
import org.springframework.stereotype.Component;

@Component("topProjectByDayMetric")
public class TopProjectByDayMetric extends DayCountMetric {

    @Resource
    private RecordReader<AccessRecord, String> projectIdReader;

    private final List<RecordFilter<AccessRecord>> filters = Collections.unmodifiableList(Arrays.asList(
            new ProdFilter(), new UserIdFilter()));

    @Resource
    private DayCountWriter dayCountWriter;

    @Override
    public List<RecordFilter<AccessRecord>> getFilters() {
        return filters;
    }

    @Override
    public RecordReader<AccessRecord, String> getRecordReader() {
        return projectIdReader;
    }

    @Override
    public void write(AccessRecord record) {
        dayCountWriter.writeMetric(record, this);
    }
}
