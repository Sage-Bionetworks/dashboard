package org.sagebionetworks.dashboard.parse;

public class CertifiedUserIdReader implements RecordReader<CuPassingRecord, String> {

    @Override
    public String read(CuPassingRecord record) {
        final String userId = record.userId();
        if (userId == null || userId.isEmpty()) {
            return "null-user-id";
        }
        return userId;
    }
}
