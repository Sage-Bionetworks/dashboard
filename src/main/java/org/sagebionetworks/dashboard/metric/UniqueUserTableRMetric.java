package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.ClientFilter;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UriTableFilter;
import org.sagebionetworks.dashboard.parse.UserIdReader;
import org.sagebionetworks.dashboard.service.UniqueCountWriter;
import org.springframework.stereotype.Component;

/**
 * This metric captures the number of unique users that use R Client to access a table.
 */
@Component("uniqueUserTableRMetric")
public class UniqueUserTableRMetric extends UniqueCountMetric<AccessRecord, String> {

    private final List<RecordFilter<AccessRecord>> filters = 
            Collections.unmodifiableList(Arrays.asList( new ProdFilter(),
                    new UriTableFilter(), ClientFilter.R));

    private final RecordReader<AccessRecord, String> reader = new UserIdReader();

    @Resource
    private UniqueCountWriter<AccessRecord> uniqueCountWriter;

    @Override
    public List<RecordFilter<AccessRecord>> getFilters() {
        return filters;
    }

    @Override
    public RecordReader<AccessRecord, String> getRecordReader() {
        return reader;
    }

    @Override
    public void write(AccessRecord record) {
        uniqueCountWriter.writeMetric(record, this);
    }
}
