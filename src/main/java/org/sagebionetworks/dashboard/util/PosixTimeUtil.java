package org.sagebionetworks.dashboard.util;

/**
 * Measured as the number of seconds that have elapsed since
 * 00:00:00 Coordinated Universal Time (UTC), Thursday, 1 January 1970.
 */
public class PosixTimeUtil {

    public static final long MINUTE = 60L;
    public static final long MINUTE_3 = MINUTE * 3L;
    public static final long HOUR = MINUTE * 60L;
    public static final long DAY = HOUR * 24L;

    public static long floorToMinute(long posixTime) {
        return (posixTime / MINUTE) * MINUTE;
    }

    public static long floorToMinute3(long posixTime) {
        return (posixTime / MINUTE_3) * MINUTE_3;
    }

    public static long floorToHour(long posixTime) {
        return (posixTime / HOUR) * HOUR;
    }

    public static long floorToDay(long posixTime) {
        return (posixTime / DAY) * DAY;
    }
}
