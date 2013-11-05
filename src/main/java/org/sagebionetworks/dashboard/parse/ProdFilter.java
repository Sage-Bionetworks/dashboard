package org.sagebionetworks.dashboard.parse;

public class ProdFilter implements RecordFilter {
    @Override
    public boolean matches(Record record) {
        return "prod".equalsIgnoreCase(record.getStack());
    }
}
