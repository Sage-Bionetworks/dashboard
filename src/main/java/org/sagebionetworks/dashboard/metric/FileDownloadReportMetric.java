package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UriFileDownloadFilter;
import org.sagebionetworks.dashboard.parse.UserDataReader;
import org.sagebionetworks.dashboard.parse.UserIdFilter;
import org.sagebionetworks.dashboard.service.UniqueCountWriter;
import org.sagebionetworks.dashboard.util.AccessRecordUtil;
import org.springframework.stereotype.Component;

@Component("fileDownloadReportMetric")
public class FileDownloadReportMetric extends UniqueCountMetric<AccessRecord, String>{

    private List<RecordFilter<AccessRecord>> filters = Collections.unmodifiableList(Arrays.asList(
                new ProdFilter(), new UriFileDownloadFilter(), new UserIdFilter()));

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
        uniqueCountWriter.writeMetric(record, this, ":" + AccessRecordUtil.getEntityId(record.getUri()));
    }

}
