package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.sagebionetworks.dashboard.dao.redis.KeyAssembler;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.Statistic;
import org.sagebionetworks.dashboard.util.PosixTimeUtil;

public class KeyAssemblerTest {

    @Test
    public void test() {
        KeyAssembler assembler = new KeyAssembler(Statistic.avg, Interval.m3, NameSpace.timeseries);
        String key = assembler.getKey("fU9i3", 123456789L);
        assertEquals("avg:m3:timeseries:fU9i3:123456789", key);
    }

    @Test
    public void testGetKey() {

        KeyAssembler assembler = new KeyAssembler(Statistic.n, Interval.m3, NameSpace.count);
        DateTime dt = new DateTime(2013, 12, 3, 5, 22, 17, 697, DateTimeZone.UTC);
        String key = assembler.getKey("metric", dt);
        long timestamp = PosixTimeUtil.floorToMinute3(dt);
        assertEquals("n:m3:count:metric:" + timestamp, key);

        assembler = new KeyAssembler(Statistic.n, Interval.hour, NameSpace.count);
        dt = new DateTime(2013, 12, 3, 5, 22, 17, 697, DateTimeZone.UTC);
        key = assembler.getKey("metric", dt);
        timestamp = PosixTimeUtil.floorToHour(dt);
        assertEquals("n:hour:count:metric:" + timestamp, key);

        assembler = new KeyAssembler(Statistic.n, Interval.day, NameSpace.count);
        dt = new DateTime(2013, 12, 3, 5, 22, 17, 697, DateTimeZone.UTC);
        key = assembler.getKey("metric", dt);
        timestamp = PosixTimeUtil.floorToDay(dt);
        assertEquals("n:day:count:metric:" + timestamp, key);

        assembler = new KeyAssembler(Statistic.n, Interval.week, NameSpace.count);
        dt = new DateTime(2013, 12, 3, 5, 22, 17, 697, DateTimeZone.UTC);
        key = assembler.getKey("metric", dt);
        timestamp = PosixTimeUtil.floorToWeek(dt);
        assertEquals("n:week:count:metric:" + timestamp, key);

        assembler = new KeyAssembler(Statistic.n, Interval.month, NameSpace.count);
        dt = new DateTime(2013, 12, 3, 5, 22, 17, 697, DateTimeZone.UTC);
        key = assembler.getKey("metric", dt);
        timestamp = PosixTimeUtil.floorToMonth(dt);
        assertEquals("n:month:count:metric:" + timestamp, key);

        assembler = new KeyAssembler(Statistic.n, Interval.sage_quarter, NameSpace.count);
        dt = new DateTime(2013, 12, 3, 5, 22, 17, 697, DateTimeZone.UTC);
        key = assembler.getKey("metric", dt);
        timestamp = PosixTimeUtil.floorToSageQuarter(dt);
        assertEquals("n:sage_quarter:count:metric:" + timestamp, key);
    }

    @Test
    public void testGetTimestamps() {

        KeyAssembler assembler = new KeyAssembler(Statistic.n, Interval.m3, NameSpace.count);
        DateTime from = new DateTime(2013, 12, 3, 5, 22, 17, 697, DateTimeZone.UTC);
        DateTime to = new DateTime(2013, 12, 3, 5, 22, 17, 697, DateTimeZone.UTC);
        List<Long> timestamps = assembler.getTimestamps(from, to);
        assertNotNull(timestamps);
        assertEquals(1, timestamps.size());

        to = to.plusMinutes(1);
        timestamps = assembler.getTimestamps(from, to);
        assertNotNull(timestamps);
        assertEquals(1, timestamps.size());

        to = to.plusMinutes(2);
        timestamps = assembler.getTimestamps(from, to);
        assertNotNull(timestamps);
        assertEquals(2, timestamps.size());

        to = to.minusMinutes(5);
        timestamps = assembler.getTimestamps(from, to);
        assertNotNull(timestamps);
        assertEquals(0, timestamps.size());

        assembler = new KeyAssembler(Statistic.n, Interval.hour, NameSpace.count);
        from = new DateTime(2013, 12, 3, 5, 22, 17, 697, DateTimeZone.UTC);
        to = new DateTime(2013, 12, 3, 7, 22, 17, 697, DateTimeZone.UTC);
        timestamps = assembler.getTimestamps(from, to);
        assertNotNull(timestamps);
        assertEquals(3, timestamps.size());

        assembler = new KeyAssembler(Statistic.n, Interval.day, NameSpace.count);
        from = new DateTime(2013, 12, 3, 5, 22, 17, 697, DateTimeZone.UTC);
        to = new DateTime(2013, 12, 5, 7, 22, 17, 697, DateTimeZone.UTC);
        timestamps = assembler.getTimestamps(from, to);
        assertNotNull(timestamps);
        assertEquals(3, timestamps.size());

        assembler = new KeyAssembler(Statistic.n, Interval.week, NameSpace.count);
        from = new DateTime(2013, 12, 3, 5, 22, 17, 697, DateTimeZone.UTC);
        to = new DateTime(2013, 12, 11, 7, 22, 17, 697, DateTimeZone.UTC);
        timestamps = assembler.getTimestamps(from, to);
        assertNotNull(timestamps);
        assertEquals(2, timestamps.size());

        assembler = new KeyAssembler(Statistic.n, Interval.month, NameSpace.count);
        from = new DateTime(2013, 12, 3, 5, 22, 17, 697, DateTimeZone.UTC);
        to = new DateTime(2014, 1, 3, 7, 22, 17, 697, DateTimeZone.UTC);
        timestamps = assembler.getTimestamps(from, to);
        assertNotNull(timestamps);
        assertEquals(2, timestamps.size());

        to = from;
        timestamps = assembler.getTimestamps(from, to);
        assertNotNull(timestamps);
        assertEquals(1, timestamps.size());

        to = to.plusMonths(2);
        timestamps = assembler.getTimestamps(from, to);
        assertNotNull(timestamps);
        assertEquals(3, timestamps.size());

        to = to.minusMonths(5);
        timestamps = assembler.getTimestamps(from, to);
        assertNotNull(timestamps);
        assertEquals(0, timestamps.size());

        assembler = new KeyAssembler(Statistic.n, Interval.sage_quarter, NameSpace.count);
        from = new DateTime(2013, 12, 3, 5, 22, 17, 697, DateTimeZone.UTC);
        to = new DateTime(2013, 12, 3, 7, 22, 17, 697, DateTimeZone.UTC);
        timestamps = assembler.getTimestamps(from, to);
        assertNotNull(timestamps);
        assertEquals(1, timestamps.size());

        to = to.plusMonths(4);
        timestamps = assembler.getTimestamps(from, to);
        assertNotNull(timestamps);
        assertEquals(2, timestamps.size());
    }
}
