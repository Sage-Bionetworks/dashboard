package org.sagebionetworks.dashboard.parse;

import org.sagebionetworks.dashboard.model.AccessRecord;

public class ClientReader implements RecordReader<AccessRecord, String> {

    @Override
    public String read(AccessRecord record) {
        String userAgent = record.getUserAgent();
        if (userAgent != null && !userAgent.isEmpty()) {
            return userAgent.toLowerCase();
        } else {
            return "null-client";
        }
    }
}
