package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.ClientReader;
import org.sagebionetworks.dashboard.parse.ErrorFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UserIdFilter;
import org.springframework.stereotype.Component;

@Component("errorByClientMetric")
public class ErrorByClientMetric extends UniqueCountMetric {

    private final List<RecordFilter> filters = Collections.unmodifiableList(Arrays.asList(
            new ProdFilter(), new ErrorFilter(), new UserIdFilter()));

    private final RecordReader<String> reader = new ClientReader();

    @Override
    public List<RecordFilter> getFilters() {
        return filters;
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return reader;
    }
}
