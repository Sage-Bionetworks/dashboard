package org.sagebionetworks.dashboard.parse;

public interface RecordFilter<R extends Record> {
    boolean matches(R record);
}
