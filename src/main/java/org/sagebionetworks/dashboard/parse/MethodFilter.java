package org.sagebionetworks.dashboard.parse;

import org.sagebionetworks.dashboard.model.AccessRecord;

public class MethodFilter implements RecordFilter<AccessRecord> {

    public MethodFilter(String... methods) {
        this.methods = methods;
    }

    @Override
    public boolean matches(AccessRecord record) {
        for (String method : methods) {
            if (method.equalsIgnoreCase(record.getMethod())) {
                return true;
            }
        }
        return false;
    }

    private final String[] methods;
}
