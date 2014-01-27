package org.sagebionetworks.dashboard.http.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class SynapseClientTest {

    @Test
    public void test() {

        SynapseClient client = new SynapseClient();
        String session = client.login();
        assertNotNull(session);
        assertFalse(session.isEmpty());

        // Synapse User Guide
        String entityName = client.getEntityName("syn1669771", session);
        assertNotNull(entityName);
        assertFalse(entityName.isEmpty());
        entityName = client.getEntityName("nothing", session);
        assertNull(entityName);

        // Anonymous user
        String userName = client.getUserName("273950", session);
        assertNotNull(userName);
        assertFalse(userName.isEmpty());
        userName = client.getUserName("nobody", session);
        assertNull(userName);
    }
}
