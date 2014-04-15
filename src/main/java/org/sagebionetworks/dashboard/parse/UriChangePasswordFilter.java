package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriChangePasswordFilter extends UriRegexFilter {
    public UriChangePasswordFilter() {
        super(Pattern.compile("^/auth/v1/userPassword$", Pattern.CASE_INSENSITIVE));
    }
}
