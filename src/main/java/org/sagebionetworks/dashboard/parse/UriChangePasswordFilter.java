package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriChangePasswordFilter extends UriRegexFilter {

    private static final Pattern PATTERN = Pattern.compile(
            "^/auth/v1/userPassword$", Pattern.CASE_INSENSITIVE);

    public UriChangePasswordFilter() {
        super(PATTERN);
    }
}
