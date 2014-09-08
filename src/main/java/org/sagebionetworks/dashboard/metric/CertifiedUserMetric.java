package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.CertifiedUserFilter;
import org.sagebionetworks.dashboard.parse.CertifiedUserIdReader;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("certifiedUserMetric")
public class CertifiedUserMetric extends UniqueCountMetric {

    private RecordReader<String> reader = new CertifiedUserIdReader();

    private final List<RecordFilter> filters = Collections.unmodifiableList(Arrays.asList(
            (RecordFilter) new CertifiedUserFilter()));

    @Override
    public List<RecordFilter> getFilters() {
        return filters ;
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return reader;
    }    
}
