package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UriIdReader;
import org.sagebionetworks.dashboard.parse.UriTableFilter;
import org.sagebionetworks.dashboard.service.UniqueCountWriter;
import org.springframework.stereotype.Component;

@Component("uniqueTableMetric")
public class UniqueTableMetric extends UniqueCountMetric<AccessRecord, String> {

    private static final Pattern SYN_ID = Pattern.compile("syn\\d+", Pattern.CASE_INSENSITIVE);

    private final List<RecordFilter<AccessRecord>> filters = 
            Collections.unmodifiableList(Arrays.asList( new ProdFilter(), new UriTableFilter()));

    private final RecordReader<AccessRecord, String> reader = new UriIdReader(SYN_ID);

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
