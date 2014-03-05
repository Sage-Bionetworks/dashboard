package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriFileDownloadFilter implements RecordFilter {

    // GET /repo/v1/entity/{id}/file
    // GET /repo/v1/entity/{id}/version/{ver}/file
    private static final Pattern PATTERN = Pattern.compile(
            "^/repo/v1/entity/syn\\d+(/version/\\d+)?/file$", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean matches(Record record) {
        String uri = record.getUri();
        if (uri == null) {
            return false; // Otherwise Matcher will throw an NPE
        }
        return PATTERN.matcher(uri).matches();
    }
}
