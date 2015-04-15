package org.sagebionetworks.dashboard.parse;

import org.sagebionetworks.dashboard.model.AccessRecord;

/**
 * Matches when the record is successful.
 */
public class SuccessFilter implements RecordFilter<AccessRecord> {
    @Override
    public boolean matches(AccessRecord record) {
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
