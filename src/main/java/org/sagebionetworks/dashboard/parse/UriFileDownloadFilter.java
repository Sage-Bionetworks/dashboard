package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriFileDownloadFilter extends UriRegexFilter {

    // GET /repo/v1/entity/{id}/file
    // GET /repo/v1/entity/{id}/version/{ver}/file
    private static final Pattern PATTERN = Pattern.compile(
            "^/repo/v1/entity/syn\\d+(/version/\\d+)?/file$", Pattern.CASE_INSENSITIVE);

    public UriFileDownloadFilter() {
        super(PATTERN);
    }
}
