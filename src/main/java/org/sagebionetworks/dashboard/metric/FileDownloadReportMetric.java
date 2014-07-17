package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.sagebionetworks.dashboard.parse.UriFileDownloadInvFilter;
import org.sagebionetworks.dashboard.parse.UserDataReader;
import org.springframework.stereotype.Component;

@Component("fileDownloadReportMetric")
public class FileDownloadReportMetric implements ReportMetric{

    private List<RecordFilter> filters;

    public FileDownloadReportMetric(String entityId) {
        filters = Collections.unmodifiableList(Arrays.asList(
                new ProdFilter(), new UriFileDownloadInvFilter(entityId)));
    }

    private final RecordReader<String> reader = new UserDataReader();

    @Override
    public String getName() {
        return "fileDownloadReportMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return filters ;
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return reader;
    }

}
