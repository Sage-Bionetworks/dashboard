package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.CertifiedUserFilter;
import org.sagebionetworks.dashboard.parse.CertifiedUserIdReader;
import org.sagebionetworks.dashboard.parse.CuPassingRecord;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("certifiedUserMetric")
public class CertifiedUserMetric extends UniqueCountMetric<CuPassingRecord, String> {

    private RecordReader<CuPassingRecord, String> reader = new CertifiedUserIdReader();

    private final List<RecordFilter<CuPassingRecord>> filters = Collections.unmodifiableList(Arrays.asList(
            (RecordFilter<CuPassingRecord>) new CertifiedUserFilter()));

    @Override
    public List<RecordFilter<CuPassingRecord>> getFilters() {
        return filters ;
    }

    @Override
    public RecordReader<CuPassingRecord, String> getRecordReader() {
        return reader;
    }    
}
