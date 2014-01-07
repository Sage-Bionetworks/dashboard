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
import org.springframework.stereotype.Component;

/** Latencies of the post-entity-header REST API. */
@Component("postEntityHeaderMetric")
public class PostEntityHeaderMetric implements TimeSeriesMetric {

    @Override
    public String getName() {
        return "postEntityHeaderMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return Collections.unmodifiableList(Arrays.asList(
                new ProdFilter(), new MethodFilter("post"), new UriEntityHeaderFilter()));
    }

    @Override
    public RecordReader<Long> getRecordReader() {
        return new LatencyReader();
    }
}
