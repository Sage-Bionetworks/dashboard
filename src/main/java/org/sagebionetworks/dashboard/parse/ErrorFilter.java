package org.sagebionetworks.dashboard.parse;

/**
 * Matches when there is an error in the record.
 */
public class ErrorFilter implements RecordFilter {
    @Override
    public boolean matches(Record record) {
        String status = record.getStatus();
        if (status == null || status.isEmpty()) {
            return false; // Error or not we don't know
        }
        if ("false".equalsIgnoreCase(status)) {
            return true; // This is an error
        }
        if ("true".equalsIgnoreCase(status)) {
            return false; // This is not an error
        }
        return false; // We don't know
    }
}
