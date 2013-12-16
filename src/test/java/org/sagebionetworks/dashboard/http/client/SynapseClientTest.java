package org.sagebionetworks.dashboard.http.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class SynapseClientTest {

    @Test
    public void test() {

        SynapseClient client = new SynapseClient();
        String session = client.login();
        assertNotNull(session);
        assertFalse(session.isEmpty());

        // Synapse User Guide
        String entity = client.getEntityName("syn1669771", session);
        assertNotNull(entity);
        assertFalse(entity.isEmpty());

        // Anonymous user
        String displayName = client.getDisplayName("273950", session);
        assertNotNull(displayName);
        assertFalse(displayName.isEmpty());
    }
}
