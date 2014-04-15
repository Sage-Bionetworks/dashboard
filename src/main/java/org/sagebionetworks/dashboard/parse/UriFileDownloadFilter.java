package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriFileDownloadFilter extends UriRegexFilter {
    public UriFileDownloadFilter() {
        super(Pattern.compile(
                // GET /repo/v1/entity/{id}/file
                // GET /repo/v1/entity/{id}/version/{ver}/file
                "^/repo/v1/entity/syn\\d+(/version/\\d+)?/file$",
                Pattern.CASE_INSENSITIVE));
    }
}
