package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("topProjectByDayMetric")
public class TopProjectByDayMetric implements DayCountMetric {

    @Resource
    private RecordReader<String> projectIdReader;

    private final List<RecordFilter> filters = Collections.unmodifiableList(Arrays.asList(
            (RecordFilter)new ProdFilter()));

    @Override
    public String getName() {
        return "topProjectByDayMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return filters;
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return projectIdReader;
    }
}
