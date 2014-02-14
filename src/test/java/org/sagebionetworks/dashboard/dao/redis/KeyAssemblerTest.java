package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sagebionetworks.dashboard.dao.redis.KeyAssembler;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.Statistic;

public class KeyAssemblerTest {
    @Test
    public void test() {
        KeyAssembler assembler = new KeyAssembler(Statistic.avg, Interval.m3, NameSpace.timeseries);
        String key = assembler.getKey("fU9i3", 123456789L);
        assertEquals("avg:m3:timeseries:fU9i3:123456789", key);
    }
}
