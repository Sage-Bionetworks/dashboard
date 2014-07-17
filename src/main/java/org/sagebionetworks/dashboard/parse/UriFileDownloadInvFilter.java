package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriFileDownloadInvFilter extends UriRegexFilter {
    public UriFileDownloadInvFilter(String entityId) {
        super(Pattern.compile(
                // GET /repo/v1/entity/{id}/file
                // GET /repo/v1/entity/{id}/version/{ver}/file
                "^/repo/v1/entity/syn" + entityId + "(/version/\\d+)?/file$",
                Pattern.CASE_INSENSITIVE));
    }
}
