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
        String line = ",\"1\",\"1383058800713\",,\"repo-prod-18.prod.sagebase.org\",\"18096\",\"Synpase-Java-Client/2013-10-17-fcdf7fe-730  Synapse-Web-Client/18.0-9-g47c6675\",,\"742d6c03-121c-44d4-be19-021c20766d3a\",,\"/repo/v1/version\",\"273950\",,\"2013-10-29\",\"GET\",\"62248c222722313a:-6a4fdffe:141d8bc9b96:-7ffd\",\"000000018\",\"prod\",\"true\",\"200\"";
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
        assertEquals("200", records.get(0).getStatus());
        assertEquals(1L, records.get(0).getLatency().longValue());
        assertEquals("Synpase-Java-Client/2013-10-17-fcdf7fe-730  Synapse-Web-Client/18.0-9-g47c6675", records.get(0).getUserAgent());
        assertEquals("prod", records.get(0).getStack());
        assertEquals("repo-prod-18.prod.sagebase.org", records.get(0).getHost());
        line = "\"syn150935\",\"63\",\"1383058860156\",,\"repo-prod.prod.sagebase.org\",\"18111\",,,\"ed39b97b-95c9-4813-88c8-98115980f70f\",,\"/repo/v1/entity/syn150935\",\"2223222\",,\"2013-10-29\",\"GET\",\"62248c222722313a:-6a4fdffe:141d8bc9b96:-7ffd\",\"000000018\",\"prod\",\"true\",\"404\"";
        reader = new StringReader(line);
        records = parser.parse(reader);
        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals("syn150935", records.get(0).getObjectId());
        assertEquals(63L, records.get(0).getLatency().longValue());
        line = ",\"330\",\"1388278872156\",,\"repo-prod.prod.sagebase.org\",\"50032\",\"python-requests/1.2.3 CPython/2.7.4 Linux/3.8.0-19-generic\",\"limit=20&offset=40\",\"37dbacaa-d508-40bc-95df-027669c1defa\",,\"/repo/v1/evaluation/1917804/submission/all\",\"1976831\",,\"2013-12-29\",\"GET\",\"596b6cd4fe9c6b87:2490c0ea:142eda5a7fd:-7ffd\",\"000000024\",\"prod\",\"false\",\"500\"";
        reader = new StringReader(line);
        records = parser.parse(reader);
        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals("500", records.get(0).getStatus());
    }
}
