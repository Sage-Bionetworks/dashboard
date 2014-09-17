package org.sagebionetworks.dashboard.parse;

// TODO: This does not distinguish prod from staging
public class ProdFilter implements RecordFilter<AccessRecord> {
    @Override
    public boolean matches(AccessRecord record) {
        return "prod".equalsIgnoreCase(record.getStack());
    }
}
