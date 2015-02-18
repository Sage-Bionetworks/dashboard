package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriUpdateTableFilter extends UriRegexFilter {

    public UriUpdateTableFilter() {
        // /entity/{id}/table/append/...
        // /entity/{id}/table/deleteRows
        super(Pattern.compile("^/repo/v1/entity/syn\\d+/table/(append/(.*?)|deleteRows)",
                Pattern.CASE_INSENSITIVE));
    }
}
