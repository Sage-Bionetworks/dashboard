package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class WikiObjectIdReader implements RecordReader<String> {

    private static final Pattern OBJ_ID = Pattern.compile("(syn)?\\d+", Pattern.CASE_INSENSITIVE);
    private final RecordReader<String> uriIdReader = new UriIdReader(OBJ_ID);

    /**
     * Reads the Wiki object ID from the record. The object can be either an entity or an evaluation.
     * Note that object ID field in some records has the wiki ID, which is not read here.
     */
    @Override
    public String read(AccessRecord record) {
        return uriIdReader.read(record);
    }
}
