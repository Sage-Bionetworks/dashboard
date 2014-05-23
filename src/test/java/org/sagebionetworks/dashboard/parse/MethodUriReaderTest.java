package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MethodUriReaderTest {
    @Test
    public void test() {
        RepoRecord record = new RepoRecord();
        RecordReader<String> reader = new MethodUriReader();
        assertEquals("null-method null-uri", reader.read(record));
        record.setUri("/REPO/v1/SYN12345");
        assertEquals("null-method /repo/v1/{id}", reader.read(record));
        record.setUri("/auth/v1");
        assertEquals("null-method /auth/v1", reader.read(record));
        record.setUri("/auth/v1/");
        assertEquals("null-method /auth/v1/", reader.read(record));
        record.setUri("/auth/v1/user");
        assertEquals("null-method /auth/v1/user", reader.read(record));
        record.setUri("/auth/v1/user/123982");
        assertEquals("null-method /auth/v1/user/{id}", reader.read(record));
        record.setMethod("GET");
        assertEquals("get /auth/v1/user/{id}", reader.read(record));
        record.setUri("/repo/v1/entity/syn123/wiki2");
        assertEquals("get /repo/v1/entity/{id}/wiki2", reader.read(record));
        record.setUri("/repo/v1/entity/syn123/wiki2/321");
        assertEquals("get /repo/v1/entity/{id}/wiki2/{id}", reader.read(record));
        record.setUri("/repo/v1/entity/syn123/wikiheadertree2");
        assertEquals("get /repo/v1/entity/{id}/wikiheadertree2", reader.read(record));
        record.setUri("/repo/v1/entity/md5/4462fb9bd6837f0b6f7c5ae4c4348bfa");
        assertEquals("get /repo/v1/entity/md5/{md5}", reader.read(record));
    }
}
