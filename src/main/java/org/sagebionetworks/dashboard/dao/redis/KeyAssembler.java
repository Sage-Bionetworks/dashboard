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

        long ts = -1L;
        switch(interval) {
            case m3:
                ts = PosixTimeUtil.floorToMinute3(timestamp);
                break;
            case hour:
                ts = PosixTimeUtil.floorToHour(timestamp);
                break;
            case day:
                ts = PosixTimeUtil.floorToDay(timestamp);
                break;
            case week:
                ts = PosixTimeUtil.floorToWeek(timestamp);
                break;
            case month:
                ts = PosixTimeUtil.floorToMonth(timestamp);
                break;
            case sage_quarter:
                ts = PosixTimeUtil.floorToSageQuarter(timestamp);
                break;
            default:
                throw new RuntimeException("Interval " + interval + " not supported.");
        }
        return getKey(metricId, ts);
    }

    List<Long> getTimestamps(final String metricId, final DateTime from, final DateTime to) {

        long start = -1L;
        long end = -1L;
        long step = -1L;
        switch (interval) {
            case m3:
                 start = PosixTimeUtil.floorToMinute3(from);
                 end = PosixTimeUtil.floorToMinute3(to);
                 step = PosixTimeUtil.MINUTE_3;
                 return getTimestamps(start, end, step);
            case hour:
                start = PosixTimeUtil.floorToHour(from);
                end = PosixTimeUtil.floorToHour(to);
                step = PosixTimeUtil.HOUR;
                return getTimestamps(start, end, step);
            case day:
                start = PosixTimeUtil.floorToDay(from);
                end = PosixTimeUtil.floorToDay(to);
                step = PosixTimeUtil.DAY;
                return getTimestamps(start, end, step);
            case week:
                start = PosixTimeUtil.floorToWeek(from);
                end = PosixTimeUtil.floorToWeek(to);
                step = PosixTimeUtil.WEEK;
                return getTimestamps(start, end, step);
            case month:
                start = PosixTimeUtil.floorToMonth(from);
                end = PosixTimeUtil.floorToMonth(to);
                return getTimestampsByMonths(start, end, 1, from.getZone());
            case sage_quarter:
                start = PosixTimeUtil.floorToSageQuarter(from);
                end = PosixTimeUtil.floorToSageQuarter(to);
                return getTimestampsByMonths(start, end, 3, from.getZone());
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
