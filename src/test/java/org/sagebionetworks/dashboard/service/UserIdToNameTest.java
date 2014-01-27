package org.sagebionetworks.dashboard.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.springframework.test.util.ReflectionTestUtils;

public class UserIdToNameTest {

    @Test
    public void test() {

        // Mock
        SynapseDao mockSynapse = mock(SynapseDao.class);
        when(mockSynapse.getUserName("id")).thenReturn("name");
        when(mockSynapse.getUserName("null")).thenReturn(null);
        UserIdToName idToName = new UserIdToName();
        ReflectionTestUtils.setField(idToName, "synapseDao", mockSynapse, SynapseDao.class);

        // Test
        CountDataPoint in = new CountDataPoint("id", 5L);
        CountDataPoint out = idToName.convert(in);
        assertEquals("name", out.getId());
        assertEquals(5L, out.getCount());
        in = new CountDataPoint("null", 3L);
        out = idToName.convert(in);
        assertEquals("null", out.getId());
        assertEquals(3L, out.getCount());
    }
}
