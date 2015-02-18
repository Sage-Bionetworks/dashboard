package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriTableFilter extends UriRegexFilter {

    public UriTableFilter() {
        // /entity/{id}/table/...
        super(Pattern.compile("^/repo/v1/entity/syn\\d+/table/(.*?)",
                Pattern.CASE_INSENSITIVE));
    }

}
