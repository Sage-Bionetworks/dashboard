package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.MethodFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UriWiki2Filter;
import org.sagebionetworks.dashboard.parse.UserIdFilter;
import org.sagebionetworks.dashboard.parse.UserIdReader;
import org.springframework.stereotype.Component;

@Component("wikiReadByUserMetric")
public class WikiReadByUserMetric extends DayCountMetric {

    private final List<RecordFilter<AccessRecord>> filters = Collections.unmodifiableList(Arrays.asList(
            new ProdFilter(), new UriWiki2Filter(), new MethodFilter("GET"),
            new UserIdFilter()));

    private final RecordReader<AccessRecord, String> reader = new UserIdReader();

    @Override
    public List<RecordFilter<AccessRecord>> getFilters() {
        return filters;
    }

    @Override
    public RecordReader<AccessRecord, String> getRecordReader() {
        return reader;
    }
}
