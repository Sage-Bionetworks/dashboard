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

public class MetricCollection implements Iterable<Metric<AccessRecord, ?>>{

    @Resource
    private  Collection<SimpleCountMetric> simpleCountMetrics;

    @Resource
    private  Collection<TimeSeriesMetric> timeSeriesMetrics;

    @Resource
    private  Collection<UniqueCountMetric<AccessRecord, String>> uniqueCountMetrics;

    @Resource
    private  Collection<DayCountMetric> dayCountMetrics;

    private Iterator<SimpleCountMetric> simpleCountMetricPointer;
    private  Iterator<TimeSeriesMetric> timeSeriesMetricPointer;
    private  Iterator<UniqueCountMetric<AccessRecord, String>> uniqueCountMetricPointer;
    private  Iterator<DayCountMetric> dayCountMetricPointer;

    // reset all the pointers
    public MetricCollection() {
        simpleCountMetricPointer = simpleCountMetrics.iterator();
        timeSeriesMetricPointer = timeSeriesMetrics.iterator();
        uniqueCountMetricPointer = uniqueCountMetrics.iterator();
        dayCountMetricPointer = dayCountMetrics.iterator();
    }

    @Override
    public Iterator<Metric<AccessRecord, ?>> iterator() {
        return new MetricCollectionIterator();
    }

    class MetricCollectionIterator implements Iterator<Metric<AccessRecord, ?>> {

        @Override
        public boolean hasNext() {
            return dayCountMetricPointer.hasNext();
        }

        @Override
        public Metric<AccessRecord, ?> next() {
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

        @Override
        public void remove() {
            // do nothing
        }
    }
}
