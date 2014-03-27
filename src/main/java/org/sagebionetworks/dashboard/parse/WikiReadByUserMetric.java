package org.sagebionetworks.dashboard.parse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.metric.DayCountMetric;
import org.springframework.stereotype.Component;

@Component("wikiReadByUserMetric")
public class WikiReadByUserMetric implements DayCountMetric {

    private final List<RecordFilter> filters = Collections.unmodifiableList(Arrays.asList(
            new ProdFilter(), new UriWiki2Filter(), new MethodFilter("GET")));

    private final RecordReader<String> reader = new UserIdReader();

    @Override
    public String getName() {
        return "wikiReadByUserMetric";
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
