package org.sagebionetworks.dashboard.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.ClientReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("topClientMetric")
public class TopClientMetric implements UniqueCountToWrite {

    @Override
    public String getName() {
        return "topClientMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return Collections.unmodifiableList(Arrays.asList(
                (RecordFilter)new ProdFilter()));
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return new ClientReader();
    }
}
