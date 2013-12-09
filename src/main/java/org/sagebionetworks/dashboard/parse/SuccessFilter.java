package org.sagebionetworks.dashboard.parse;

/**
 * Matches when the record is successful.
 */
public class SuccessFilter implements RecordFilter {
    @Override
    public boolean matches(Record record) {
        String status = record.getStatus();
        if (status == null || status.isEmpty()) {
            return false; // Success or not we don't know
        }
        if ("false".equalsIgnoreCase(status)) {
            return false; // This is an failure
        }
        if ("true".equalsIgnoreCase(status)) {
            return true; // This is a success
        }
        return false; // We don't know
    }
}
