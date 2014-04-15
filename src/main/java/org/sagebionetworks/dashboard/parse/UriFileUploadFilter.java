package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriFileUploadFilter extends UriRegexFilter {

    // POST /file/v1/createChunkedFileUploadToken
    // POST /file/v1/fileHandle
    private static final Pattern PATTERN = Pattern.compile(
            "^/file/v1/(createChunkedFileUploadToken|fileHandle)$", Pattern.CASE_INSENSITIVE);

    public UriFileUploadFilter() {
        super(PATTERN);
    }
}
