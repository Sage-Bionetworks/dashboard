package org.sagebionetworks.dashboard.parse;

public class UserIdReader implements RecordReader<String> {

    @Override
    public String read(Record record) {
        final String userId = record.getUserId();
        if (userId == null || userId.isEmpty()) {
            return "null-user-id";
        }
        return userId;
    }
}
