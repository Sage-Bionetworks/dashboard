package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriUploadTableFilter extends UriRegexFilter {

    public UriUploadTableFilter() {
        // /entity/{id}/table/upload/...
        super(Pattern.compile("^/repo/v1/entity/syn\\d+/table/upload/(.*?)",
                Pattern.CASE_INSENSITIVE));
    }
}
