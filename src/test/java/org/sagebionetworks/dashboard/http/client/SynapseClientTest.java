package org.sagebionetworks.dashboard.http.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
        String displayName = client.getDisplayName("273950", session);
        assertNotNull(displayName);
        assertFalse(displayName.isEmpty());
        displayName = client.getDisplayName("nobody", session);
        assertNull(displayName);
    }

    @Test
    public void testTeam() {

        SynapseClient client = new SynapseClient();
        String session = client.login();
        assertNotNull(session);

        String email = "dashboard@sagebase.org";
        Long userId = client.getPrincipalId("dashboard@sagebase.org", session);
        assertNotNull(userId);

        Long teamId = client.getTeamId(userId, session);
        assertNotNull(teamId);

        assertTrue(client.isMemberOfTeam(email, teamId, session));
        assertFalse(client.isMemberOfTeam("jojoojoj@nowhere.com", teamId, session));
    }
}
