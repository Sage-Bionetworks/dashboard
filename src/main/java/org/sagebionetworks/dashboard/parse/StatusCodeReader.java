package org.sagebionetworks.dashboard.parse;

import org.sagebionetworks.dashboard.model.AccessRecord;

public class StatusCodeReader implements RecordReader<AccessRecord, String> {
    @Override
    public String read(AccessRecord record) {
        return record.getStatus();
    }
}
