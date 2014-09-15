package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.MethodFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.TimeFilter;
import org.sagebionetworks.dashboard.parse.UriCuqSubmitFilter;
import org.sagebionetworks.dashboard.parse.UserIdFilter;
import org.sagebionetworks.dashboard.parse.UserIdReader;
import org.springframework.stereotype.Component;

@Component("certifiedUserQuizSubmitMetric")
public class CertifiedUserQuizSubmitMetric extends UniqueCountMetric {   

    private RecordReader<String> reader = new UserIdReader();

    private final List<RecordFilter> filters = Collections.unmodifiableList(Arrays.asList(
            new MethodFilter("post"), new UriCuqSubmitFilter(), new TimeFilter(1403827200000L),
            new UserIdFilter()));

    @Override
    public List<RecordFilter> getFilters() {
        return filters ;
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return reader;
    }   
}
