package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.StringReader;
import java.util.List;

import org.junit.Test;

public class RepoRecordParserTest {

    @Test
    public void test() {
        RecordParser parser = new RepoRecordParser();
        String line = ",\"1\",\"1383058800713\",,\"repo-prod-18.prod.sagebase.org\",\"18096\",\"Synpase-Java-Client/2013-10-17-fcdf7fe-730  Synapse-Web-Client/18.0-9-g47c6675\",,\"742d6c03-121c-44d4-be19-021c20766d3a\",,\"/repo/v1/version\",\"273950\",,\"2013-10-29\",\"GET\",\"62248c222722313a:-6a4fdffe:141d8bc9b96:-7ffd\",\"000000018\",\"prod\",\"true\"";
        StringReader reader = new StringReader(line);
        List<Record> records = parser.parse(reader);
        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals("742d6c03-121c-44d4-be19-021c20766d3a", records.get(0).getSessionId());
        assertEquals(1383058800713L, records.get(0).getTimestamp().longValue());
        assertEquals("273950", records.get(0).getUserId());
        assertNull(records.get(0).getObjectId());
        assertEquals("GET", records.get(0).getMethod());
        assertEquals("/repo/v1/version", records.get(0).getUri());
        assertEquals("true", records.get(0).getStatus());
        assertEquals(1L, records.get(0).getLatency().longValue());
        assertEquals("Synpase-Java-Client/2013-10-17-fcdf7fe-730  Synapse-Web-Client/18.0-9-g47c6675", records.get(0).getUserAgent());
        assertEquals("prod", records.get(0).getStack());
        assertEquals("repo-prod-18.prod.sagebase.org", records.get(0).getHost());
        line = "\"syn150935\",\"63\",\"1383058860156\",,\"repo-prod.prod.sagebase.org\",\"18111\",,,\"ed39b97b-95c9-4813-88c8-98115980f70f\",,\"/repo/v1/entity/syn150935\",\"2223222\",,\"2013-10-29\",\"GET\",\"62248c222722313a:-6a4fdffe:141d8bc9b96:-7ffd\",\"000000018\",\"prod\",\"true\"";
        reader = new StringReader(line);
        records = parser.parse(reader);
        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals("syn150935", records.get(0).getObjectId());
        assertEquals(63L, records.get(0).getLatency().longValue());
    }
}
