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
        if (status.startsWith("4") || status.startsWith("5")) {
            return false;
        }
        if (status.startsWith("2") || status.startsWith("3")) {
            return true;
        }
        return false; // We don't know
    }
}
