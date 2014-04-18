package org.sagebionetworks.dashboard.parse;

public class StatusCodeReader implements RecordReader<String> {
    @Override
    public String read(Record record) {
        return record.getStatus();
    }
}
