package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.LatencyReader;
import org.sagebionetworks.dashboard.parse.MethodFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UriEntityHeaderFilter;
import org.sagebionetworks.dashboard.parse.UserIdFilter;
import org.springframework.stereotype.Component;

/** Latencies of the post-entity-header REST API. */
@Component("postEntityHeaderMetric")
public class PostEntityHeaderMetric extends TimeSeriesMetric {

    private final List<RecordFilter> filters = Collections.unmodifiableList(Arrays.asList(
            new ProdFilter(), new MethodFilter("post"), new UriEntityHeaderFilter(),
            new UserIdFilter()));

    private final RecordReader<Long> reader = new LatencyReader();

    @Override
    public List<RecordFilter> getFilters() {
        return filters;
    }

    @Override
    public RecordReader<Long> getRecordReader() {
        return reader;
    }
}
