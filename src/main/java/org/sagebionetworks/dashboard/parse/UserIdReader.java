package org.sagebionetworks.dashboard.parse;

public class UserIdReader implements RecordReader<AccessRecord, String> {

    @Override
    public String read(AccessRecord record) {
        final String userId = record.getUserId();
        if (userId == null || userId.isEmpty()) {
            return "null-user-id";
        }
        return userId;
    }
}
