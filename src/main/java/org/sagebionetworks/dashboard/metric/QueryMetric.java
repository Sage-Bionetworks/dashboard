package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.LatencyReader;
import org.sagebionetworks.dashboard.parse.MethodFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UriQueryFilter;
import org.springframework.stereotype.Component;

/** Latencies of the query REST API. */
@Component("queryMetric")
public class QueryMetric implements TimeSeriesMetric {

    private final List<RecordFilter> filters = Collections.unmodifiableList(Arrays.asList(
            new ProdFilter(), new MethodFilter("get"), new UriQueryFilter()));

    private final RecordReader<Long> reader = new LatencyReader();

    @Override
    public String getName() {
        return "queryMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return filters;
    }

    @Override
    public RecordReader<Long> getRecordReader() {
        return reader;
    }
}
