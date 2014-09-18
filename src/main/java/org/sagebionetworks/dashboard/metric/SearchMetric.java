package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.LatencyReader;
import org.sagebionetworks.dashboard.parse.MethodFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UriSearchFilter;
import org.sagebionetworks.dashboard.parse.UserIdFilter;
import org.sagebionetworks.dashboard.service.TimeSeriesWriter;
import org.springframework.stereotype.Component;

/** Latencies of the search REST API. */
@Component("searchMetric")
public class SearchMetric extends TimeSeriesMetric {

    private final List<RecordFilter<AccessRecord>> filters = Collections.unmodifiableList(Arrays.asList(
            new ProdFilter(), new MethodFilter("post"), new UriSearchFilter(),
            new UserIdFilter()));

    private final RecordReader<AccessRecord, Long> reader = new LatencyReader();

    @Resource
    private TimeSeriesWriter timeSeriesWriter;

    @Override
    public List<RecordFilter<AccessRecord>> getFilters() {
        return filters;
    }

    @Override
    public RecordReader<AccessRecord, Long> getRecordReader() {
        return reader;
    }

    @Override
    public void write(AccessRecord record) {
        timeSeriesWriter.writeMetric(record, this);
    }
}
