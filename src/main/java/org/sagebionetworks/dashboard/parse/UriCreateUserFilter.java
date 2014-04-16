package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriCreateUserFilter extends UriRegexFilter {
    public UriCreateUserFilter() {
        super(Pattern.compile("^/auth/v1/user$", Pattern.CASE_INSENSITIVE));
    }
}
