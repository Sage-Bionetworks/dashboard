package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriGetDescendantsFilter extends UriRegexFilter {

    // Covers 2 APIs:
    // "/repo/v1/entity/{id}/descendants"
    // "/repo/v1/entity/{id}/descendants/{generation}"
    private static final Pattern PATTERN = Pattern.compile(
            "^/repo/v1/entity/syn\\d+/descendants(/\\d+)?", Pattern.CASE_INSENSITIVE);

    public UriGetDescendantsFilter() {
        super(PATTERN);
    }
}
