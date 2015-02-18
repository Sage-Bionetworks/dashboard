package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriDownloadTableFilter extends UriRegexFilter {

    public UriDownloadTableFilter() {
        // /entity/{id}/table/download/...
        super(Pattern.compile("^/repo/v1/entity/syn\\d+/table/download/(.*?)",
                Pattern.CASE_INSENSITIVE));
    }
}
