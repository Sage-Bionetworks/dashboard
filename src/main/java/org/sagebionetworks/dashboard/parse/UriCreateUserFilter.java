package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriCreateUserFilter extends UriRegexFilter {

    private static final Pattern PATTERN = Pattern.compile(
            "^/auth/v1/user$", Pattern.CASE_INSENSITIVE);

    public UriCreateUserFilter() {
        super(PATTERN);
    }
}
