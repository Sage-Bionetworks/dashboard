package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.QuestionFailFilter;
import org.sagebionetworks.dashboard.parse.QuestionIndexReader;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("questionPassMetric")
public class QuestionFailMetric implements UniqueCountMetric {

    private List<RecordFilter> filters = Collections.unmodifiableList(Arrays.asList(
            (RecordFilter) new QuestionFailFilter()));;
    private RecordReader<String> reader = new QuestionIndexReader();

    @Override
    public String getName() {
        return "questionPassMetric";
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
