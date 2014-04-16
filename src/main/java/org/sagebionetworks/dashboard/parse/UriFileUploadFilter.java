package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriFileUploadFilter extends UriRegexFilter {
    public UriFileUploadFilter() {
        super(Pattern.compile(
                // POST /file/v1/createChunkedFileUploadToken
                // POST /file/v1/fileHandle
                "^/file/v1/(createChunkedFileUploadToken|fileHandle)$",
                Pattern.CASE_INSENSITIVE));
    }
}
