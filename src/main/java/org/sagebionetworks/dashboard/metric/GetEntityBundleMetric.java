package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.LatencyReader;
import org.sagebionetworks.dashboard.parse.MethodFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UriEntityBundleFilter;
import org.sagebionetworks.dashboard.parse.UserIdFilter;
import org.springframework.stereotype.Component;

/** Latencies of the get-entity-bundle REST API. */
@Component("getEntityBundleMetric")
public class GetEntityBundleMetric extends TimeSeriesMetric {

    private final List<RecordFilter<AccessRecord>> filters = Collections.unmodifiableList(Arrays.asList(
            new ProdFilter(), new MethodFilter("get"), new UriEntityBundleFilter(),
            new UserIdFilter()));

    private final RecordReader<AccessRecord, Long> reader = new LatencyReader();

    @Override
    public List<RecordFilter<AccessRecord>> getFilters() {
        return filters;
    }

    @Override
    public RecordReader<AccessRecord, Long> getRecordReader() {
        return reader;
    }
}
