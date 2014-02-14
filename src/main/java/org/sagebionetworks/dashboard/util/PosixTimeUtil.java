package org.sagebionetworks.dashboard.util;

import org.joda.time.DateTime;

/**
 * POSIX time is measured as the number of seconds that have elapsed since
 * 00:00:00 Coordinated Universal Time (UTC), Thursday, 1 January 1970. Note
 * that one second is the minimal scale we keep track of the metrics.
 */
public final class PosixTimeUtil {

    public static final long MINUTE = 60L;
    public static final long MINUTE_3 = MINUTE * 3L; // 3 minutes
    public static final long HOUR = MINUTE * 60L;
    public static final long DAY = HOUR * 24L;
    public static final long WEEK = DAY * 7L;

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

    public static long floorToMinute(DateTime dateTime) {
        return floorToMinute(getPosixTime(dateTime));
    }

    public static long floorToMinute3(DateTime dateTime) {
        return floorToMinute3(getPosixTime(dateTime));
    }

    public static long floorToHour(DateTime dateTime) {
        return floorToHour(getPosixTime(dateTime));
    }

    public static long floorToDay(DateTime dateTime) {
        return floorToDay(getPosixTime(dateTime));
    }

    public static long floorToWeek(DateTime dateTime) {
        final int days = dateTime.getDayOfWeek() % 7;
        DateTime sunday = dateTime.minusDays(days);
        return floorToDay(sunday);
    }

    public static long floorToMonth(DateTime dateTime) {
        final int days = dateTime.getDayOfMonth() - 1;
        DateTime day1 = dateTime.minusDays(days);
        return floorToDay(day1);
    }

    public static long floorToSageQuarter(DateTime dateTime) {
        final int sageOffset = 1;  // Example: Feb - Apr should all floor to Feb 1
        final int months = (dateTime.getMonthOfYear() + sageOffset) % 3;
        return floorToMonth(dateTime.minusMonths(months));
    }

    private static long getPosixTime(DateTime dateTime) {
        return dateTime.getMillis() / 1000L;
    }

    private PosixTimeUtil() {};
}
