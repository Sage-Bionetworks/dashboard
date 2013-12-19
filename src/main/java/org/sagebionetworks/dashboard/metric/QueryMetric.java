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

    @Override
    public String getName() {
        return "queryMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return Collections.unmodifiableList(Arrays.asList(
                new ProdFilter(), new MethodFilter("get"), new UriQueryFilter()));
    }

    @Override
    public RecordReader<Long> getRecordReader() {
        return new LatencyReader();
    }
}
