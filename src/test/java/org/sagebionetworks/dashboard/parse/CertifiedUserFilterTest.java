package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

public class CertifiedUserFilterTest {

    @Test(expected=NullPointerException.class)
    public void nullPassingRecord() {
        CuPassingRecord record = null;
        CertifiedUserFilter filter = new CertifiedUserFilter();
        filter.matches(record);
    }

    @Test
    public void pass() {
        CuPassingRecord record = new CuPassingRecord(true, "1", new DateTime(), 10);
        CertifiedUserFilter filter = new CertifiedUserFilter();
        assertTrue(filter.matches(record));
    }

    @Test
    public void fail() {
        CuPassingRecord record = new CuPassingRecord(false, "1", new DateTime(), 10);
        CertifiedUserFilter filter = new CertifiedUserFilter();
        assertTrue(!filter.matches(record));
    }
}
