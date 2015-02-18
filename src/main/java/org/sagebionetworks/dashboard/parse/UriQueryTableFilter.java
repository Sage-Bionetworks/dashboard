package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriQueryTableFilter extends UriRegexFilter {

    public UriQueryTableFilter() {
        // /entity/{id}/table/getRows
        // /entity/{id}/table/query...
        super(Pattern.compile("^/repo/v1/entity/syn\\d+/table/(getRows|query(.*?))",
                Pattern.CASE_INSENSITIVE));
    }
}
