package org.sagebionetworks.dashboard.model.redis;

import static org.sagebionetworks.dashboard.model.redis.RedisKey.SEPARATOR;

/**
 * Assembles Redis keys at runtime in the format of
 * {metric}:{aggregation}:{name-space}:{id}:{time-stamp}.
 */
public class RedisKeyAssembler {

    public RedisKeyAssembler(String metric, String aggregation, String nameSpace) {
        prefix = metric + SEPARATOR + aggregation + SEPARATOR + nameSpace;
    }

    public String getKey(String clientId, long flooredPosixTime) {
        return prefix + SEPARATOR + clientId + SEPARATOR + flooredPosixTime;
    }

    private final String prefix;
}
