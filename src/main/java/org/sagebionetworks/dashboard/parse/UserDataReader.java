package org.sagebionetworks.dashboard.parse;

public class UserDataReader implements RecordReader<String> {

    @Override
    public String read(Record record) {
        Long timestamp = record.getTimestamp();
        String userId = (new UserIdReader()).read(record);
        String client = (new ClientSummaryReader()).read(record);
        return Long.toString(timestamp) + ":" + userId + ":" + client;
    }
}
