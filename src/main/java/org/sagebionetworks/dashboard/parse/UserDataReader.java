package org.sagebionetworks.dashboard.parse;


public class UserDataReader implements RecordReader<String> {

    @Override
    public String read(Record record) {
        ClientSummaryReader clientReader = new ClientSummaryReader();

        Long timestamp = record.getTimestamp();
        String userId = record.getUserId();
        String client = clientReader.read(record);
        return Long.toString(timestamp) + ":" + userId + ":" + client;
    }
}
