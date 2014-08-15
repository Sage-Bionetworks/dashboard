package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

public class CertifiedUserIdReaderTest {

    @Test(expected=NullPointerException.class)
    public void nullPassingRecord() {
        CuPassingRecord record = null;
        CertifiedUserIdReader reader = new CertifiedUserIdReader();
        reader.read(record);
    }

    @Test
    public void nullUserId() {
        CuPassingRecord record = new CuPassingRecord(true, null, new DateTime(), 10);
        CertifiedUserIdReader reader = new CertifiedUserIdReader();
        assertEquals(reader.read(record), "null-user-id");
    }

    @Test
    public void emptyUserId() {
        CuPassingRecord record = new CuPassingRecord(true, "", new DateTime(), 10);
        CertifiedUserIdReader reader = new CertifiedUserIdReader();
        assertEquals(reader.read(record), "null-user-id");
    }

    @Test
    public void successUserId() {
        CuPassingRecord record = new CuPassingRecord(true, "12345", new DateTime(), 10);
        CertifiedUserIdReader reader = new CertifiedUserIdReader();
        assertEquals(reader.read(record), "12345");
    }
}
