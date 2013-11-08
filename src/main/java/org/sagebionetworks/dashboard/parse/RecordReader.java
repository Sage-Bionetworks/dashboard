package org.sagebionetworks.dashboard.parse;

public interface RecordReader<T> {
    T read(Record record);
}
