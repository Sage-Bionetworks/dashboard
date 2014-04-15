package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriEntityBundleFilter extends UriRegexFilter {
    public UriEntityBundleFilter() {
        super(Pattern.compile(
                "^/repo/v1/entity/syn\\d+/(version/\\d+/)?bundle",
                Pattern.CASE_INSENSITIVE));
    }
}
