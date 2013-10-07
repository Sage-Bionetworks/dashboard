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
}
