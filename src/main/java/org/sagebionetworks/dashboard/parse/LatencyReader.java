package org.sagebionetworks.dashboard.parse;

public class LatencyReader implements RecordReader<Long> {
    @Override
    public Long read(Record record) {
        return record.getLatency();
    }
}
