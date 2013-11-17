package org.sagebionetworks.dashboard.model;

import static org.sagebionetworks.dashboard.model.Key.SEPARATOR;

/**
 * Assembles Redis keys at runtime in the format of
 * {statistic}:{aggregation}:{name-space}:{metric}:{time-stamp}.
 */
public class KeyAssembler {

    public KeyAssembler(Statistic statistic, Aggregation aggregation, NameSpace nameSpace) {
        prefix = statistic + SEPARATOR + aggregation + SEPARATOR + nameSpace;
    }

    public String getKey(String metricId, long flooredPosixTime) {
        return prefix + SEPARATOR + metricId + SEPARATOR + flooredPosixTime;
    }

    private final String prefix;
}
