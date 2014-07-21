package org.sagebionetworks.dashboard.parse;

public class UserDataReader implements RecordReader<String> {

    @Override
    public String read(Record record) {
        Long timestamp = record.getTimestamp();
        String userId = record.getUserId();
        String client = (new ClientSummaryReader()).read(record);
        return Long.toString(timestamp) + ":" + userId + ":" + client;
    }
}
