package org.sagebionetworks.dashboard.parse;

import org.sagebionetworks.dashboard.model.AccessRecord;

public class LatencyReader implements RecordReader<AccessRecord, Long> {
    @Override
    public Long read(AccessRecord record) {
        return record.getLatency();
    }
}
