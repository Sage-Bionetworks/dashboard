package org.sagebionetworks.dashboard.model.redis;

import static org.sagebionetworks.dashboard.model.redis.Key.SEPARATOR;

/**
 * Assembles Redis keys at runtime in the format of
 * {statistic}:{aggregation}:{name-space}:{id}:{time-stamp}.
 */
public class KeyAssembler {

    public KeyAssembler(Statistic statistic, Aggregation aggregation, NameSpace nameSpace) {
        prefix = statistic + SEPARATOR + aggregation + SEPARATOR + nameSpace;
    }

    public String getKey(String clientId, long flooredPosixTime) {
        return prefix + SEPARATOR + clientId + SEPARATOR + flooredPosixTime;
    }

    private final String prefix;
}
