package org.sagebionetworks.dashboard.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityIdReader implements RecordReader<String> {

    private static final Pattern SYN_ID = Pattern.compile(
            "syn[1-9][0-9]*", Pattern.CASE_INSENSITIVE);

    /**
     * Reads the entity ID from the record. It tries in the following order:
     * <ol>
     * <li> The object ID field.
     * <li> Any matches in the request URI.
     * <li> Any matches in the query string.
     * </ol>
     */
    @Override
    public String read(Record record) {
        // Object ID
        final String entityId = record.getObjectId();
        if (entityId != null && !entityId.isEmpty()
                && SYN_ID.matcher(entityId).matches()) {
            return entityId;
        }
        // Request URI
        final String uri = record.getUri();
        if (uri != null && !uri.isEmpty()) {
            Matcher matcher = SYN_ID.matcher(uri);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        // Query String
        final String query = record.getQueryString();
        if (query != null && !query.isEmpty()) {
            Matcher matcher = SYN_ID.matcher(query);
            if (matcher.find()) {
                return matcher.group().toLowerCase();
            }
        }
        return null;
    }
}
