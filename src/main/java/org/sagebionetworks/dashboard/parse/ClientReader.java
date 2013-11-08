package org.sagebionetworks.dashboard.parse;

public class ClientReader implements RecordReader<String> {

    @Override
    public String read(Record record) {
        String userAgent = record.getUserAgent();
        if (userAgent != null && !userAgent.isEmpty()) {
            return userAgent.toLowerCase();
        } else {
            return "null-client";
        }
    }
}
