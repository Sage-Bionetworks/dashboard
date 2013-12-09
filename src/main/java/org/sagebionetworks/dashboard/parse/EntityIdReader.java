package org.sagebionetworks.dashboard.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityIdReader implements RecordReader<String> {

    private static final Pattern SYN_ID = Pattern.compile(
            "syn[1-9][0-9]*", Pattern.CASE_INSENSITIVE);

    /**
     * Tries to read the entity ID from the record. It tries the object ID
     * field first. If it is not there, tries to read it from the URI.
     */
    @Override
    public String read(Record record) {
        final String entityId = record.getObjectId();
        if (entityId != null && !entityId.isEmpty()) {
            if (SYN_ID.matcher(entityId).matches()) {
                return entityId;
            }
        }
        final String uri = record.getUri();
        if (uri != null && !uri.isEmpty()) {
            Matcher matcher = SYN_ID.matcher(uri);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }
}
