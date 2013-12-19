package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.ErrorFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("errorCountMetric")
public class ErrorCountMetric implements SimpleCountMetric {

    @Override
    public String getName() {
        return "errorCountMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return Collections.unmodifiableList(Arrays.asList(
                new ProdFilter(), new ErrorFilter()));
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return new RecordReader<String>(){
            @Override
            public String read(Record record) {
                return "";
            }};
    }
}
