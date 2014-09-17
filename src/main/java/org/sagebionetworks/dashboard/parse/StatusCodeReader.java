package org.sagebionetworks.dashboard.parse;

public class StatusCodeReader implements RecordReader<AccessRecord, String> {
    @Override
    public String read(AccessRecord record) {
        return record.getStatus();
    }
}
