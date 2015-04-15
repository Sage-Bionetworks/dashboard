package org.sagebionetworks.dashboard.parse;

import org.sagebionetworks.dashboard.model.AccessRecord;

public class TableUriReader implements RecordReader<AccessRecord, String> {

    private final UriRegexFilter UPDATE_FILTER = new UriUpdateTableFilter();
    private final UriRegexFilter QUERY_FILTER = new UriQueryTableFilter();
    private final UriRegexFilter DOWNLOAD_FILTER = new UriDownloadTableFilter();
    private final UriRegexFilter UPLOAD_FILTER = new UriUploadTableFilter();

    @Override
    public String read(AccessRecord record) {
        if (UPDATE_FILTER.matches(record)) {
            return "Update";
        }
        if (QUERY_FILTER.matches(record)) {
            return "Query";
        }
        if (DOWNLOAD_FILTER.matches(record)) {
            return "Download";
        }
        if (UPLOAD_FILTER.matches(record)) {
            return "Upload";
        }
        return "Uncategorized";
    }
}
