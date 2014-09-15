package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UriFileDownloadFilter;
import org.sagebionetworks.dashboard.parse.UserDataReader;
import org.sagebionetworks.dashboard.parse.UserIdFilter;
import org.springframework.stereotype.Component;

@Component("fileDownloadReportMetric")
public class FileDownloadReportMetric extends ReportMetric{

    private List<RecordFilter> filters = Collections.unmodifiableList(Arrays.asList(
                new ProdFilter(), new UriFileDownloadFilter(), new UserIdFilter()));

    private final RecordReader<String> reader = new UserDataReader();

    @Override
    public List<RecordFilter> getFilters() {
        return filters ;
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return reader;
    }

}
