package org.sagebionetworks.dashboard.parse;

import org.sagebionetworks.dashboard.model.Record;

public interface RecordReader<R extends Record, V> {

    /**
     * Reads the value from the record. This may return null when the value
     * is missing in the record.
     */
    V read(R record);
}
