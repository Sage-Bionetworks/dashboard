package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.CuResponseRecord;
import org.sagebionetworks.dashboard.parse.QuestionFailFilter;
import org.sagebionetworks.dashboard.parse.QuestionIndexReader;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("questionFailMetric")
public class QuestionFailMetric extends QuestionMetric {

    private List<RecordFilter<CuResponseRecord>> filters = Collections.unmodifiableList(Arrays.asList(
            (RecordFilter<CuResponseRecord>) new QuestionFailFilter()));;
    private RecordReader<CuResponseRecord, String> reader = new QuestionIndexReader();

    @Override
    public List<RecordFilter<CuResponseRecord>> getFilters() {
        return filters;
    }

    @Override
    public RecordReader<CuResponseRecord, String> getRecordReader() {
        return reader;
    }

}
