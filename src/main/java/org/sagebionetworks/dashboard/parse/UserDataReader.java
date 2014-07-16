package org.sagebionetworks.dashboard.parse;

public class UserDataReader implements RecordReader<String> {

    @Override
    public String read(Record record) {
        UserIdReader userIdReader = new UserIdReader();
        ClientSummaryReader clientReader = new ClientSummaryReader();

        String timestamp = record.getTimestamp().toString();
        String userId = userIdReader.read(record);
        String client = clientReader.read(record);
        return timestamp + ":" + userId + ":" + client;
    }
}
