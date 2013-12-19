package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.MethodUriReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("topMethodMetric")
public class TopMethodMetric implements UniqueCountMetric {

    @Override
    public String getName() {
        return "topMethodMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return Collections.unmodifiableList(Arrays.asList(
                (RecordFilter)new ProdFilter()));
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return new MethodUriReader();
    }
}