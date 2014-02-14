package org.sagebionetworks.dashboard.util;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

public class PosixTimeUtilTest {

    @Test
    public void test() {

        final DateTime dt = new DateTime(2005, 3, 26, 15, 37, 51, 132, DateTimeZone.UTC);
        final long posix = dt.getMillis() / 1000L;

        long min = PosixTimeUtil.floorToMinute(posix);
        DateTime expected = new DateTime(2005, 3, 26, 15, 37, 0, 0, DateTimeZone.UTC);
        assertEquals(expected.getMillis() / 1000L, min);
        min = PosixTimeUtil.floorToMinute(dt);
        assertEquals(expected.getMillis() / 1000L, min);

        long min3 = PosixTimeUtil.floorToMinute3(posix);
        expected = new DateTime(2005, 3, 26, 15, 36, 0, 0, DateTimeZone.UTC);
        assertEquals(expected.getMillis() / 1000L, min3);
        min3 = PosixTimeUtil.floorToMinute3(dt);
        assertEquals(expected.getMillis() / 1000L, min3);

        long hour = PosixTimeUtil.floorToHour(posix);
        expected = new DateTime(2005, 3, 26, 15, 0, 0, 0, DateTimeZone.UTC);
        assertEquals(expected.getMillis() / 1000L, hour);
        hour = PosixTimeUtil.floorToHour(dt);
        assertEquals(expected.getMillis() / 1000L, hour);

        long day = PosixTimeUtil.floorToDay(posix);
        expected = new DateTime(2005, 3, 26, 0, 0, 0, 0, DateTimeZone.UTC);
        assertEquals(expected.getMillis() / 1000L, day);
        day = PosixTimeUtil.floorToDay(dt);
        assertEquals(expected.getMillis() / 1000L, day);
    }

    @Test
    public void testIncrement() {

        DateTime dateTime = new DateTime(2005, 3, 26, 12, 3, 25, 983, DateTimeZone.UTC);
        long posix = dateTime.getMillis() / 1000L;
        long hour12 = PosixTimeUtil.floorToHour(dateTime);

        dateTime = new DateTime(2005, 3, 26, 11, 32, 21, 356, DateTimeZone.UTC);
        posix = dateTime.getMillis() / 1000L;
        long hour11 = PosixTimeUtil.floorToHour(posix);
        assertEquals(60L * 60L, hour12 - hour11);
    }

    @Test
    public void testFloorToWeek() {

        DateTime sun = new DateTime(2014, 2, 9, 0, 0, 2, DateTimeZone.UTC);
        DateTime mon = new DateTime(2014, 2, 10, 12, 3, 25, 983, DateTimeZone.UTC);
        DateTime thu = new DateTime(2014, 2, 13, 12, 3, 25, 983, DateTimeZone.UTC);
        DateTime sat = new DateTime(2014, 2, 15, 23, 59, 59, 999, DateTimeZone.UTC);

        // Should floor to Sunday 00:00
        DateTime floor = new DateTime(2014, 2, 9, 0, 0, 0, DateTimeZone.UTC);
        assertEquals(floor.getMillis() / 1000L, PosixTimeUtil.floorToWeek(sun));
        assertEquals(floor.getMillis() / 1000L, PosixTimeUtil.floorToWeek(mon));
        assertEquals(floor.getMillis() / 1000L, PosixTimeUtil.floorToWeek(thu));
        assertEquals(floor.getMillis() / 1000L, PosixTimeUtil.floorToWeek(sat));
        assertEquals(floor.getMillis() / 1000L, PosixTimeUtil.floorToWeek(floor));
    }

    @Test
    public void testFloorToMonth() {

        DateTime day1 = new DateTime(2014, 1, 1, 0, 0, 2, DateTimeZone.UTC);
        DateTime day12 = new DateTime(2014, 1, 12, 9, 3, 25, 983, DateTimeZone.UTC);
        DateTime day31 = new DateTime(2014, 1, 31, 9, 3, 25, 983, DateTimeZone.UTC);

        // Should floor to day 1 00:00
        DateTime floor = new DateTime(2014, 1, 1, 0, 0, 0, DateTimeZone.UTC);
        assertEquals(floor.getMillis() / 1000L, PosixTimeUtil.floorToMonth(day1));
        assertEquals(floor.getMillis() / 1000L, PosixTimeUtil.floorToMonth(day12));
        assertEquals(floor.getMillis() / 1000L, PosixTimeUtil.floorToMonth(day31));
        assertEquals(floor.getMillis() / 1000L, PosixTimeUtil.floorToMonth(floor));
    }

    @Test
    public void testFloorToSageQuarter() {

        DateTime jan = new DateTime(2014, 1, 1, 0, 0, 2, DateTimeZone.UTC);
        DateTime feb = new DateTime(2014, 2, 12, 9, 3, 25, 983, DateTimeZone.UTC);
        DateTime mar = new DateTime(2014, 3, 31, 9, 3, 25, 983, DateTimeZone.UTC);
        DateTime apr = new DateTime(2014, 4, 1, 9, 3, 25, 983, DateTimeZone.UTC);
        DateTime may = new DateTime(2014, 5, 22, 9, 3, 25, 983, DateTimeZone.UTC);

        // Should floor to day 1 00:00
        DateTime floorNov = new DateTime(2013, 11, 1, 0, 0, 0, DateTimeZone.UTC);
        DateTime floorFeb = new DateTime(2014, 2, 1, 0, 0, 0, DateTimeZone.UTC);
        DateTime floorMay = new DateTime(2014, 5, 1, 0, 0, 0, DateTimeZone.UTC);
        assertEquals(floorNov.getMillis() / 1000L, PosixTimeUtil.floorToSageQuarter(jan));
        assertEquals(floorFeb.getMillis() / 1000L, PosixTimeUtil.floorToSageQuarter(feb));
        assertEquals(floorFeb.getMillis() / 1000L, PosixTimeUtil.floorToSageQuarter(mar));
        assertEquals(floorFeb.getMillis() / 1000L, PosixTimeUtil.floorToSageQuarter(apr));
        assertEquals(floorMay.getMillis() / 1000L, PosixTimeUtil.floorToSageQuarter(may));
        assertEquals(floorMay.getMillis() / 1000L, PosixTimeUtil.floorToSageQuarter(floorMay));
    }
}
