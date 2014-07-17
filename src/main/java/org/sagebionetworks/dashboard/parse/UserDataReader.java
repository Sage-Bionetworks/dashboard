package org.sagebionetworks.dashboard.parse;

import org.sagebionetworks.dashboard.dao.redis.Key;

public class UserDataReader implements RecordReader<String> {

    @Override
    public String read(Record record) {
        Long timestamp = record.getTimestamp();
        String userId = record.getUserId();
        String client = (new ClientSummaryReader()).read(record);
        return Long.toString(timestamp) + Key.SEPARATOR + userId + Key.SEPARATOR + client;
    }
}
