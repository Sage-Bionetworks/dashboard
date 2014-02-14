package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.Key.SEPARATOR;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.Statistic;
import org.sagebionetworks.dashboard.util.PosixTimeUtil;

/**
 * Assembles Redis keys at runtime in the format of
 * {statistic}:{interval}:{name-space}:{metric}:{time-stamp}.
 */
class KeyAssembler {

    KeyAssembler(Statistic statistic, Interval interval, NameSpace nameSpace) {
        if (statistic == null) {
            throw new IllegalArgumentException("Statistic cannot be null.");
        }
        if (interval == null) {
            throw new IllegalArgumentException("Interval cannot be null.");
        }
        if (nameSpace == null) {
            throw new IllegalArgumentException("Name space cannot be null.");
        }
        this.interval = interval;
        prefix = statistic + SEPARATOR + interval + SEPARATOR + nameSpace;
    }

    String getKey(String metricId, long flooredPosixTime) {
        return prefix + SEPARATOR + metricId + SEPARATOR + flooredPosixTime;
    }

    String getKey(final String metricId, final DateTime timestamp) {
        switch(interval) {
            case m3:
                return getKey(metricId, PosixTimeUtil.floorToMinute3(timestamp));
            case hour:
                return getKey(metricId, PosixTimeUtil.floorToHour(timestamp));
            case day:
                return getKey(metricId, PosixTimeUtil.floorToDay(timestamp));
            case week:
                return getKey(metricId, PosixTimeUtil.floorToWeek(timestamp));
            case month:
                return getKey(metricId, PosixTimeUtil.floorToMonth(timestamp));
            case sage_quarter:
                return getKey(metricId, PosixTimeUtil.floorToSageQuarter(timestamp));
            default:
                throw new RuntimeException("Interval " + interval + " not supported.");
        }
    }

    List<Long> getTimestamps(final String metricId, final DateTime from, final DateTime to) {
        switch (interval) {
            case m3:
                return getTimestamps(
                        PosixTimeUtil.floorToMinute3(from),
                        PosixTimeUtil.floorToMinute3(to),
                        PosixTimeUtil.MINUTE_3);
            case hour:
                return getTimestamps(
                        PosixTimeUtil.floorToHour(from),
                        PosixTimeUtil.floorToHour(to),
                        PosixTimeUtil.HOUR);
            case day:
                return getTimestamps(
                        PosixTimeUtil.floorToDay(from),
                        PosixTimeUtil.floorToDay(to),
                        PosixTimeUtil.DAY);
            case week:
                return getTimestamps(
                        PosixTimeUtil.floorToWeek(from),
                        PosixTimeUtil.floorToWeek(to),
                        PosixTimeUtil.WEEK);
            case month:
                return getTimestampsByMonths(
                        PosixTimeUtil.floorToMonth(from),
                        PosixTimeUtil.floorToMonth(to), 1, from.getZone());
            case sage_quarter:
                return getTimestampsByMonths(
                        PosixTimeUtil.floorToSageQuarter(from),
                        PosixTimeUtil.floorToSageQuarter(to), 3, from.getZone());
            default:
                throw new RuntimeException("Interval " + interval + " not supported.");
        }
    }

    private List<Long> getTimestamps(final long start, final long end, final long step) {
        List<Long> timestamps = new ArrayList<Long>();
        for (long i = start; i <= end; i += step) {
            timestamps.add(i);
        }
        return timestamps;
    }

    private List<Long> getTimestampsByMonths(final long start, final long end,
            final int months, final DateTimeZone dtZone) {
        List<Long> timestamps = new ArrayList<Long>();
        long next = start;
        while (next <= end) {
            timestamps.add(next);
            DateTime dt = new DateTime(next * 1000L, dtZone);
            dt = dt.plusMonths(months);
            next = dt.getMillis() / 1000L;
        }
        return timestamps;
    }

    private final Interval interval;
    private final String prefix; // Cached prefix string
}
