package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

import org.sagebionetworks.dashboard.model.AccessRecord;

/**
 * Extracts the ID value from the query string field.
 */
public class QueryStringIdReader extends RegexRecordReader {

    public QueryStringIdReader(Pattern pattern) {
        super(pattern);
    }

    @Override
    String readString(AccessRecord record) {
        return record.getQueryString();
    }
}
