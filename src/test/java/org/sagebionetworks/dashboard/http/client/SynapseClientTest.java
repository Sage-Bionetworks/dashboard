package org.sagebionetworks.dashboard.http.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SynapseClientTest {

    @Resource
    private SynapseClient synapseClient;

    @Test
    public void test() {

        String session = synapseClient.login();
        assertNotNull(session);
        assertFalse(session.isEmpty());

        // Synapse User Guide
        String entityName = synapseClient.getEntityName("syn1669771", session);
        assertNotNull(entityName);
        assertFalse(entityName.isEmpty());
        entityName = synapseClient.getEntityName("nothing", session);
        assertNull(entityName);

        // Anonymous user
        String userName = synapseClient.getUserName("273950", session);
        assertNotNull(userName);
        assertFalse(userName.isEmpty());
        userName = synapseClient.getUserName("nobody", session);
        assertNull(userName);
    }

    @Test
    public void testGetUsers() {

        String session = synapseClient.login();
        assertNotNull(session);
        assertFalse(session.isEmpty());

        List<SynapseUser> users = synapseClient.getUsers(0, 10, session);
        assertNotNull(users);
        assertEquals(10, users.size());
        for (SynapseUser user : users) {
            assertTrue(user.getUserId().matches("\\d+"));
            if (user.getEmail() != null) {
                assertTrue(user.getEmail().contains("@"));
            }
        }
    }

    @Test
    public void testGetBenector() {
        String session = synapseClient.login();
        assertNotNull(session);
        assertFalse(session.isEmpty());
        String benefactor = synapseClient.getBenefactor("syn2330782", session);
        assertEquals("syn2344867", benefactor);
    }

    @Test
    public void testGetProject() {
        String session = synapseClient.login();
        assertNotNull(session);
        assertFalse(session.isEmpty());
        // This is the root -- should get back nothing
        String project = synapseClient.getProject("syn4489", session);
        assertNull(project);
    }
}
