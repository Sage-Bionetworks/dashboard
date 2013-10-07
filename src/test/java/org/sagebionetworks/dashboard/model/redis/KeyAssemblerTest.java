package org.sagebionetworks.dashboard.model.redis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KeyAssemblerTest {
    @Test
    public void test() {
        KeyAssembler assembler = new KeyAssembler(Statistic.avg, Aggregation.minute_3, NameSpace.client);
        String key = assembler.getKey("fU9i3", 123456789L);
        assertEquals("avg:minute_3:client:fU9i3:123456789", key);
    }
}
