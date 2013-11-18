package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.Key.SEPARATOR;

import org.sagebionetworks.dashboard.model.Aggregation;
import org.sagebionetworks.dashboard.model.Statistic;

/**
 * Assembles Redis keys at runtime in the format of
 * {statistic}:{aggregation}:{name-space}:{metric}:{time-stamp}.
 */
class KeyAssembler {

    KeyAssembler(Statistic statistic, Aggregation aggregation, NameSpace nameSpace) {
        prefix = statistic + SEPARATOR + aggregation + SEPARATOR + nameSpace;
    }

    String getKey(String metricId, long flooredPosixTime) {
        return prefix + SEPARATOR + metricId + SEPARATOR + flooredPosixTime;
    }

    private final String prefix;
}
