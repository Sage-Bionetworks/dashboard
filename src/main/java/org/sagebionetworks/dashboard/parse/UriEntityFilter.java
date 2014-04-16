package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriEntityFilter extends UriRegexFilter {
    public UriEntityFilter() {
        // POST /entity
        // PUT  /entity/{id}
        super(Pattern.compile("^/repo/v1/entity(/syn\\d+)?$",
                Pattern.CASE_INSENSITIVE));
    }
}
