package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.MethodFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.SuccessFilter;
import org.sagebionetworks.dashboard.parse.UriChangePasswordFilter;
import org.sagebionetworks.dashboard.parse.UserIdReader;
import org.springframework.stereotype.Component;

@Component("changePasswordMetric")
public class ChangePasswordMetric extends UniqueCountMetric {

    private final List<RecordFilter> filters = Collections.unmodifiableList(Arrays.asList(
            new ProdFilter(),
            new SuccessFilter(),
            new MethodFilter("post"),
            new UriChangePasswordFilter()));

    // TODO: DASH-77. UserIdReader is not useful here.
    // Object ID and user ID always null during 'POST password'
    private final RecordReader<String> reader = new UserIdReader();

    @Override
    public List<RecordFilter> getFilters() {
        return filters;
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return reader;
    }
}
