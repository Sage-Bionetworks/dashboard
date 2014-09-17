package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class EntityIdReader implements RecordReader<AccessRecord, String> {

    private static final Pattern SYN_ID = Pattern.compile("syn\\d+", Pattern.CASE_INSENSITIVE);

    private final RecordReader<AccessRecord, String> objIdReader = new ObjectIdReader(SYN_ID);
    private final RecordReader<AccessRecord, String> uriIdReader = new UriIdReader(SYN_ID);
    private final RecordReader<AccessRecord, String> qryIdReader = new QueryStringIdReader(SYN_ID);

    /**
     * Reads the entity ID from the record. It tries in the following order:
     * <ol>
     * <li> The object ID field.
     * <li> Any matches in the request URI.
     * <li> Any matches in the query string.
     * </ol>
     */
    @Override
    public String read(AccessRecord record) {
        // Object ID
        String entityId = objIdReader.read(record);
        // Request URI
        if (entityId == null) {
            entityId = uriIdReader.read(record);
        }
        // Query String
        if (entityId == null) {
            entityId = qryIdReader.read(record);
        }
        return entityId;
    }
}
