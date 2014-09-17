package org.sagebionetworks.dashboard.parse;

public class LatencyReader implements RecordReader<AccessRecord, Long> {
    @Override
    public Long read(AccessRecord record) {
        return record.getLatency();
    }
}
