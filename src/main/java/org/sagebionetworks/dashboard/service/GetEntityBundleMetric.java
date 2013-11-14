package org.sagebionetworks.dashboard.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.LatencyReader;
import org.sagebionetworks.dashboard.parse.MethodFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UriEntityBundleFilter;
import org.springframework.stereotype.Component;

@Component("getEntityBundleMetric")
public class GetEntityBundleMetric implements TimeSeriesToWrite {

    @Override
    public String getName() {
        return "getEntityBundleMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return Collections.unmodifiableList(Arrays.asList(
                new ProdFilter(), new MethodFilter("get"), new UriEntityBundleFilter()));
    }

    @Override
    public RecordReader<Long> getRecordReader() {
        return new LatencyReader();
    }
}
