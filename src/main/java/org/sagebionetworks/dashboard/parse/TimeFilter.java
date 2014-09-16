package org.sagebionetworks.dashboard.parse;

public class TimeFilter implements RecordFilter{

    public TimeFilter(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean matches(AccessRecord record) {
        return record.getTimestamp() >= timestamp;
    }

    private final long timestamp;
}
