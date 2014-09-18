package org.sagebionetworks.dashboard.util;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.metric.DayCountMetric;
import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.metric.SimpleCountMetric;
import org.sagebionetworks.dashboard.metric.TimeSeriesMetric;
import org.sagebionetworks.dashboard.metric.UniqueCountMetric;
import org.sagebionetworks.dashboard.parse.AccessRecord;

public class MetricCollectionUtil {

    @Resource
    private static Collection<SimpleCountMetric> simpleCountMetrics;

    @Resource
    private static Collection<TimeSeriesMetric> timeSeriesMetrics;

    @Resource
    private static Collection<UniqueCountMetric<AccessRecord, String>> uniqueCountMetrics;

    @Resource
    private static Collection<DayCountMetric> dayCountMetrics;

    private static Iterator<SimpleCountMetric> simpleCountMetricPointer;
    private static Iterator<TimeSeriesMetric> timeSeriesMetricPointer;
    private static Iterator<UniqueCountMetric<AccessRecord, String>> uniqueCountMetricPointer;
    private static Iterator<DayCountMetric> dayCountMetricPointer;

    // reset all the pointers
    public static void reset() {
        simpleCountMetricPointer = simpleCountMetrics.iterator();
        timeSeriesMetricPointer = timeSeriesMetrics.iterator();
        uniqueCountMetricPointer = uniqueCountMetrics.iterator();
        dayCountMetricPointer = dayCountMetrics.iterator();
    }

    /**
     * @return true if there is another metric in the collection that haven't been retrieved
     */
    public static boolean hasNext() {
        return dayCountMetricPointer.hasNext();
    }

    // return the next metric
    public static Metric<?,?> nextMetric() {
        if (simpleCountMetricPointer.hasNext()) {
            return simpleCountMetricPointer.next();
        }
        if (timeSeriesMetricPointer.hasNext()) {
            return timeSeriesMetricPointer.next();
        }
        if (uniqueCountMetricPointer.hasNext()) {
            return uniqueCountMetricPointer.next();
        }
        return dayCountMetricPointer.next();
    }
}
