package org.sagebionetworks.dashboard.model.redis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.redis.RedisKey.Aggregation;
import org.sagebionetworks.dashboard.model.redis.RedisKey.Metric;
import org.sagebionetworks.dashboard.model.redis.RedisKey.NameSpace;

public class RedisKeyAssemblerTest {
    @Test
    public void test() {
        RedisKeyAssembler assembler = new RedisKeyAssembler(Metric.AVG, Aggregation.MINUTE3, NameSpace.CLIENT);
        String key = assembler.getKey("fU9i3", 123456789L);
        assertEquals("a:m3:c:fU9i3:123456789", key);
    }
}
