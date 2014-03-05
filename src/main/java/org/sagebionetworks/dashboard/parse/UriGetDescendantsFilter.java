package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriGetDescendantsFilter implements RecordFilter {

    // Covers 2 APIs:
    // "/repo/v1/entity/{id}/descendants"
    // "/repo/v1/entity/{id}/descendants/{generation}"
    private static final Pattern PATTERN = Pattern.compile(
            "^/repo/v1/entity/syn\\d+/descendants(/\\d+)?", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean matches(Record record) {
        String uri = record.getUri();
        if (uri == null) {
            return false; // Otherwise Matcher will throw an NPE
        }
        return PATTERN.matcher(uri).matches();
    }
}
