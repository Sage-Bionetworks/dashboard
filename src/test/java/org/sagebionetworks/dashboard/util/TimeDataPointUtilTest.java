package org.sagebionetworks.dashboard.util;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.TimeDataPoint;

public class TimeDataPointUtilTest {

    private static final TimeDataPoint dp1 = new TimeDataPoint(1, "1");
    private static final TimeDataPoint dp2 = new TimeDataPoint(0, "2");
    private static final TimeDataPoint dp3 = new TimeDataPoint(1, "3");
    private static final TimeDataPoint dp4 = new TimeDataPoint(2, "4");
    private static final TimeDataPoint dp5 = new TimeDataPoint(3, "5");
    private static final TimeDataPoint dp6 = new TimeDataPoint(4, "6");
    private static final TimeDataPoint dp7 = new TimeDataPoint(4, "7");

    @Test
    public void oneEmptyList() {

        ArrayList<TimeDataPoint> l1 = new ArrayList<TimeDataPoint>();
        ArrayList<TimeDataPoint> l2 = new ArrayList<TimeDataPoint>();
        ArrayList<TimeDataPoint> l3 = new ArrayList<TimeDataPoint>();

        // l1 = [(0, 2), (1, 1), (2, 4)]
        l1.add(dp2);
        l1.add(dp1);
        l1.add(dp4);
        // l2 = [(1, 3), (3, 5), (4, 6)]
        l2.add(dp3);
        l2.add(dp5);
        l2.add(dp6);
        // l3 = []

        List<List<TimeDataPoint>> list = new ArrayList<List<TimeDataPoint>>();
        list.add(l1);
        list.add(l2);
        list.add(l3);

        Map<String, ArrayList<String>> map = TimeDataPointUtil.createMergeMap(list, 3);

        ArrayList<String> expectedTimeStampList = new ArrayList<String>();
        expectedTimeStampList.add("0");
        expectedTimeStampList.add("1");
        expectedTimeStampList.add("2");
        expectedTimeStampList.add("3");
        expectedTimeStampList.add("4");

        List<String> tslist = TimeDataPointUtil.getMergeTimeStampList(map);
        assertArrayEquals(tslist.toArray(), 
                expectedTimeStampList.toArray());

        ArrayList<String> list1 = new ArrayList<String>();
        list1.add("2");
        list1.add("1");
        list1.add("4");
        list1.add("0");
        list1.add("0");
        assertArrayEquals(TimeDataPointUtil.getMergeValueList(tslist, map, 0).toArray(),
                list1.toArray());

        ArrayList<String> list2 = new ArrayList<String>();
        list2.add("0");
        list2.add("3");
        list2.add("0");
        list2.add("5");
        list2.add("6");
        assertArrayEquals(TimeDataPointUtil.getMergeValueList(tslist, map, 1).toArray(),
                list2.toArray());

        ArrayList<String> list3 = new ArrayList<String>();
        list3.add("0");
        list3.add("0");
        list3.add("0");
        list3.add("0");
        list3.add("0");
        assertArrayEquals(TimeDataPointUtil.getMergeValueList(tslist, map, 2).toArray(),
                list3.toArray());
    }

    @Test
    public void noneEmptyLists() {

        ArrayList<TimeDataPoint> l1 = new ArrayList<TimeDataPoint>();
        ArrayList<TimeDataPoint> l2 = new ArrayList<TimeDataPoint>();
        ArrayList<TimeDataPoint> l3 = new ArrayList<TimeDataPoint>();

        // l1 = [(0, 2), (1, 1), (2, 4)]
        l1.add(dp2);
        l1.add(dp1);
        l1.add(dp4);
        // l2 = [(1, 3), (3, 5), (4, 6)]
        l2.add(dp3);
        l2.add(dp5);
        l2.add(dp6);
        // l3 = [(4, 7)]
        l3.add(dp7);
        List<List<TimeDataPoint>> list = new ArrayList<List<TimeDataPoint>>();
        list.add(l1);
        list.add(l2);
        list.add(l3);

        Map<String, ArrayList<String>> map = TimeDataPointUtil.createMergeMap(list, 3);

        ArrayList<String> expectedTimeStampList = new ArrayList<String>();
        expectedTimeStampList.add("0");
        expectedTimeStampList.add("1");
        expectedTimeStampList.add("2");
        expectedTimeStampList.add("3");
        expectedTimeStampList.add("4");

        List<String> tslist = TimeDataPointUtil.getMergeTimeStampList(map);
        assertArrayEquals(tslist.toArray(), 
                expectedTimeStampList.toArray());

        ArrayList<String> list1 = new ArrayList<String>();
        list1.add("2");
        list1.add("1");
        list1.add("4");
        list1.add("0");
        list1.add("0");
        assertArrayEquals(TimeDataPointUtil.getMergeValueList(tslist, map, 0).toArray(),
                list1.toArray());

        ArrayList<String> list2 = new ArrayList<String>();
        list2.add("0");
        list2.add("3");
        list2.add("0");
        list2.add("5");
        list2.add("6");
        assertArrayEquals(TimeDataPointUtil.getMergeValueList(tslist, map, 1).toArray(),
                list2.toArray());

        ArrayList<String> list3 = new ArrayList<String>();
        list3.add("0");
        list3.add("0");
        list3.add("0");
        list3.add("0");
        list3.add("7");
        assertArrayEquals(TimeDataPointUtil.getMergeValueList(tslist, map, 2).toArray(),
                list3.toArray());
    }
}
