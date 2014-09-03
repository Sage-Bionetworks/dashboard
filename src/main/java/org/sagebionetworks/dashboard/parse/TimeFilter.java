package org.sagebionetworks.dashboard.parse;

public class TimeFilter implements RecordFilter{

    public TimeFilter(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean matches(Record record) {
        return record.getTimestamp() >= timestamp;
    }

    private long timestamp;
}
