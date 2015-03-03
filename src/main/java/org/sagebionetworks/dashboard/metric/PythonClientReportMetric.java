package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.ClientFilter;
import org.sagebionetworks.dashboard.parse.ClientReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UserDataReader;
import org.sagebionetworks.dashboard.parse.UserIdFilter;
import org.sagebionetworks.dashboard.service.UniqueCountWriter;
import org.springframework.stereotype.Component;

@Component("pythonClientReportMetric")
public class PythonClientReportMetric extends UniqueCountMetric<AccessRecord, String> {

    private List<RecordFilter<AccessRecord>> filters = Collections.unmodifiableList(Arrays.asList(
                new ProdFilter(), ClientFilter.PYTHON, new UserIdFilter()));

    private final RecordReader<AccessRecord, String> reader = new UserDataReader();

    @Resource
    private UniqueCountWriter<AccessRecord> uniqueCountWriter;

    @Override
    public List<RecordFilter<AccessRecord>> getFilters() {
        return filters ;
    }

    @Override
    public RecordReader<AccessRecord, String> getRecordReader() {
        return reader;
    }

    @Override
    public void write(AccessRecord record) {
        uniqueCountWriter.writeMetric(record, this, ":" + new ClientReader().read(record));
    }

}
