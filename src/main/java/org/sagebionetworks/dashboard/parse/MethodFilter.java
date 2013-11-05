package org.sagebionetworks.dashboard.parse;

public class MethodFilter implements RecordFilter {

    public MethodFilter(String method) {
        this.method = method;
    }

    @Override
    public boolean matches(Record record) {
        return method.equalsIgnoreCase(record.getMethod());
    }

    private final String method;
}
