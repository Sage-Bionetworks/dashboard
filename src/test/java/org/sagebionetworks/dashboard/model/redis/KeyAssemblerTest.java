package org.sagebionetworks.dashboard.model.redis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KeyAssemblerTest {
    @Test
    public void test() {
        KeyAssembler assembler = new KeyAssembler(Statistic.avg, Aggregation.m3, NameSpace.timeseries);
        String key = assembler.getKey("fU9i3", 123456789L);
        assertEquals("avg:m3:timeseries:fU9i3:123456789", key);
    }
}
