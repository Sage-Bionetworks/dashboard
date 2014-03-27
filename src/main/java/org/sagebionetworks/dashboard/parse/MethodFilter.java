package org.sagebionetworks.dashboard.parse;

public class MethodFilter implements RecordFilter {

    public MethodFilter(String... methods) {
        this.methods = methods;
    }

    @Override
    public boolean matches(Record record) {
        for (String method : methods) {
            if (method.equalsIgnoreCase(record.getMethod())) {
                return true;
            }
        }
        return false;
    }

    private final String[] methods;
}
