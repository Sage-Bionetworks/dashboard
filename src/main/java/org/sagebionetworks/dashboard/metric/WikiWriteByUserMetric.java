package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.MethodFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UriWiki2Filter;
import org.sagebionetworks.dashboard.parse.UserIdReader;
import org.springframework.stereotype.Component;

@Component("wikiWriteByUserMetric")
public class WikiWriteByUserMetric implements DayCountMetric {

    private final List<RecordFilter> filters = Collections.unmodifiableList(Arrays.asList(
            new ProdFilter(), new UriWiki2Filter(), new MethodFilter("POST", "PUT")));

    private final RecordReader<String> reader = new UserIdReader();

    @Override
    public String getName() {
        return "wikiWriteByUserMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return filters;
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return reader;
    }
}
