package org.sagebionetworks.dashboard.parse;

public interface RecordFilter {
    boolean matches(AccessRecord record);
}
