package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.QuestionIndexReader;
import org.sagebionetworks.dashboard.parse.QuestionPassFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("questionPassMetric")
public class QuestionPassMetric implements UniqueCountMetric {

    private List<RecordFilter> filters = Collections.unmodifiableList(Arrays.asList(
            (RecordFilter) new QuestionPassFilter()));;
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
