package org.sagebionetworks.dashboard.parse;

public class TableUriReader implements RecordReader<AccessRecord, String> {

    @Override
    public String read(AccessRecord record) {
        if (new UriUpdateTableFilter().matches(record)) {
            return "Update";
        }
        if (new UriQueryTableFilter().matches(record)) {
            return "Query";
        }
        if (new UriDownloadTableFilter().matches(record)) {
            return "Download";
        }
        if (new UriUploadTableFilter().matches(record)) {
            return "Upload";
        }
        return "Uncategorized";
    }
}
