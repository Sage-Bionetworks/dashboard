package org.sagebionetworks.dashboard.service;

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
public class ChangePasswordMetric implements UniqueCountToWrite {

    @Override
    public String getName() {
        return "changePasswordMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return Collections.unmodifiableList(Arrays.asList(
                new ProdFilter(),
                new SuccessFilter(),
                new MethodFilter("post"),
                new UriChangePasswordFilter()));
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return new UserIdReader();
    }
}
