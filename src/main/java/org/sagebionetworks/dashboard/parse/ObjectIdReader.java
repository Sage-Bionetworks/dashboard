package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

/**
 * Extracts the ID value from the object ID field.
 */
public class ObjectIdReader extends RegexRecordReader {

    public ObjectIdReader(Pattern pattern) {
        super(pattern);
    }

    @Override
    String readString(Record record) {
        return record.getObjectId();
    }
}
