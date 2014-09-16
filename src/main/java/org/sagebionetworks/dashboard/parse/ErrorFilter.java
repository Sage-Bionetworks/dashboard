package org.sagebionetworks.dashboard.parse;

/**
 * Matches when there is an error in the record.
 */
public class ErrorFilter implements RecordFilter {
    @Override
    public boolean matches(AccessRecord record) {
        String status = record.getStatus();
        if (status == null || status.isEmpty()) {
            return false; // Error or not we don't know
        }
        if (status.startsWith("4") || status.startsWith("5")) {
            return true;
        }
        if (status.startsWith("2") || status.startsWith("3")) {
            return false;
        }
        return false; // We don't know
    }
}
