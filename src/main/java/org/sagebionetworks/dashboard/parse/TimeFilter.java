package org.sagebionetworks.dashboard.parse;

import org.sagebionetworks.dashboard.model.AccessRecord;

public class TimeFilter implements RecordFilter<AccessRecord>{

    public TimeFilter(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean matches(AccessRecord record) {
        return record.getTimestamp() >= timestamp;
    }

    private final long timestamp;
}
