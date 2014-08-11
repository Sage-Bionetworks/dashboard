package org.sagebionetworks.dashboard.parse;

public class CertifiedUserIdReader implements RecordReader<String> {

    public String read(CuPassingRecord record) {
        final String userId = record.userId();
        if (userId == null || userId.isEmpty()) {
            return "null-user-id";
        }
        return userId;
    }

    @Override
    public String read(Record record) {
        throw new RuntimeException("Method is not supported.");
    }
}
