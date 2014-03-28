package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

/**
 * Extracts the ID value from the request URI field.
 */
public class UriIdReader extends PatternBasedIdReader {

    public UriIdReader(Pattern pattern) {
        super(pattern);
    }

    @Override
    String readString(Record record) {
        String uri = record.getUri();
        if (uri != null) {
            uri = uri.replace("v1", "").replace("wiki2", "");
        }
        return uri;
    }
}
