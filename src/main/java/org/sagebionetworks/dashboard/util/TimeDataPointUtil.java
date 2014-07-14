package org.sagebionetworks.dashboard.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sagebionetworks.dashboard.model.TimeDataPoint;

public class TimeDataPointUtil {

    /*
     * returns a time stamp list of a single TimeDataPoint list
     */
    private static ArrayList<String> getTimeStampList (List<TimeDataPoint> list) {
        ArrayList<String> res = new ArrayList<String>();
        for (TimeDataPoint data : list) {
            res.add(data.x());
        }
        return res;
    }

    /*
     * returns a merge time stamp list of a list of TimeDataPoint lists
     */
    private static ArrayList<String> getMergeTimeStampList (List<List<TimeDataPoint>> list) {
        Set<String> res = new HashSet<String>();
        for (List<TimeDataPoint> listdata : list) {
            ArrayList<String> tmp = getTimeStampList(listdata);
            res.addAll(tmp);
        }
        return new ArrayList<String>(res);
    }

    /**
     * 
     * @param list - the list of TimeDataPoint lists
     * @param length - the number of TimeDataPoint lists in list
     * @return a map that the keys are the time stamp
     *                and the values are the lists of values
     */
    public static Map<String, ArrayList<String>> createMergeMap (
            List<List<TimeDataPoint>> list, int length) {
        ArrayList<String> mergeTimeStampList = getMergeTimeStampList(list);
        Collections.sort(mergeTimeStampList);
        Map<String, ArrayList<String>> res = new HashMap<String, ArrayList<String>>();

        // initialize the Map
        ArrayList<String> defaultList = new ArrayList<String>();
        for (int i = 0; i < length; i++) {
            defaultList.add("0");
        }

        for (String timestamp : mergeTimeStampList) {
            res.put(timestamp, new ArrayList<String>(defaultList));
        }

        // copy the data to the map
        int count = 0;
        for (List<TimeDataPoint> listdata : list) {
            for (TimeDataPoint data : listdata) {
                ArrayList<String> tmp = res.get(data.x());
                tmp.set(count, data.y());
            }
            count++;
        }

        return res;
    }

    /**
     * 
     * @param map
     * @return the list of time stamp in the map in sorted order
     */
    public static List<String> getMergeTimeStampList (Map<String, ArrayList<String>> map) {
        ArrayList<String> tmp = new ArrayList<String>(map.keySet());
        Collections.sort(tmp);
        return tmp;
    }

    /**
     * 
     * @param mergeTimeStampList
     * @param map
     * @param nth
     * @return the nth list of values in the map
     */
    public static List<String> getMergeValueList (List<String> mergeTimeStampList, 
            Map<String, ArrayList<String>> map, int nth) {
        ArrayList<String> res = new ArrayList<String>();
        for (String timestamp: mergeTimeStampList) {
            res.add(map.get(timestamp).get(nth));
        }
        return res;
    }

}
