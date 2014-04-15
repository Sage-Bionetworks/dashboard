package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriEntityBundleFilter extends UriRegexFilter {

    private static final Pattern PATTERN = Pattern.compile(
            "^/repo/v1/entity/syn\\d+/(version/\\d+/)?bundle", Pattern.CASE_INSENSITIVE);

    public UriEntityBundleFilter() {
        super(PATTERN);
    }
}
