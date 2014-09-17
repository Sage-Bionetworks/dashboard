package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.ErrorFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UserIdFilter;
import org.springframework.stereotype.Component;

@Component("errorCountMetric")
public class ErrorCountMetric extends SimpleCountMetric {

    private final List<RecordFilter<AccessRecord>> filters = Collections.unmodifiableList(Arrays.asList(
            new ProdFilter(), new ErrorFilter(), new UserIdFilter()));

    private final RecordReader<AccessRecord, String> reader = new RecordReader<AccessRecord, String>() {
        @Override
        public String read(AccessRecord record) {
            return "";
        }
    };

    @Override
    public List<RecordFilter<AccessRecord>> getFilters() {
        return filters;
    }

    @Override
    public RecordReader<AccessRecord, String> getRecordReader() {
        return reader;
    }
}
