package org.sagebionetworks.dashboard.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.ErrorFilter;
import org.sagebionetworks.dashboard.parse.MethodUriReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("methodErrorMetric")
public class MethodErrorMetric implements UniqueCountToWrite {

    @Override
    public String getName() {
        return "methodErrorMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return Collections.unmodifiableList(Arrays.asList(
                new ProdFilter(), new ErrorFilter()));
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return new MethodUriReader();
    }
}
