package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

abstract class UriRegexFilter implements RecordFilter {

    private final Pattern pattern;

    UriRegexFilter(Pattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null.");
        }
        this.pattern = pattern;
    }

    @Override
    public boolean matches(AccessRecord record) {
        String uri = record.getUri();
        if (uri == null) {
            return false; // Otherwise Matcher will throw an NPE
        }
        return pattern.matcher(uri).matches();
    }
}
