package org.sagebionetworks.dashboard.parse;

public interface RecordReader<T> {
    
    /**
     * Reads the value from the record. This may return null when the value
     * is missing in the record.
     */
    T read(Record record);
}
