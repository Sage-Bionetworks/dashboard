package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriGetDescendantsFilter extends UriRegexFilter {
    public UriGetDescendantsFilter() {
        super(Pattern.compile(
                // /repo/v1/entity/{id}/descendants
                // /repo/v1/entity/{id}/descendants/{generation}
                "^/repo/v1/entity/syn\\d+/descendants(/\\d+)?",
                Pattern.CASE_INSENSITIVE));
    }
}
