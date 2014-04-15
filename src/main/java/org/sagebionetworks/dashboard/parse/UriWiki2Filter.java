package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriWiki2Filter extends UriRegexFilter {

    // Captures the following URLs:
    //   /entity/{ownerId}/wiki2
    //   /evaluation/{ownerId}/wiki2
    //   /entity/{ownerId}/wiki2/{wikiId}
    //   /evaluation/{ownerId}/wiki2/{wikiId}
    private static final Pattern PATTERN = Pattern.compile(
            "^/repo/v1/(entity/syn|evaluation/)\\d+/wiki2(/\\d+)?$", Pattern.CASE_INSENSITIVE);

    public UriWiki2Filter() {
        super(PATTERN);
    }
}
