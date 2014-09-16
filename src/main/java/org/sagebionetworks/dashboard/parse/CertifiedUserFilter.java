package org.sagebionetworks.dashboard.parse;

public class CertifiedUserFilter implements RecordFilter{

    public boolean matches(CuPassingRecord record) {
        return record.isPassed();
    }

    @Override
    public boolean matches(AccessRecord record) {
        throw new RuntimeException("Method is not supported.");
    }
}
