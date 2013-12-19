package org.sagebionetworks.dashboard.service;

import java.util.Collection;
import java.util.Collections;

import org.sagebionetworks.dashboard.metric.SimpleCountMetric;
import org.sagebionetworks.dashboard.metric.TimeSeriesMetric;
import org.sagebionetworks.dashboard.metric.UniqueCountMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("metricRegistry")
public class MetricRegistry {

    @Autowired
    public MetricRegistry(
            final Collection<SimpleCountMetric> sc,
            final Collection<TimeSeriesMetric> ts,
            final Collection<UniqueCountMetric> uc) {
        this.sc = Collections.unmodifiableCollection(sc);
        this.ts = Collections.unmodifiableCollection(ts);
        this.uc = Collections.unmodifiableCollection(uc);
    }

    public Collection<SimpleCountMetric> simpleCountMetrics() {
        return sc;
    }

    public Collection<TimeSeriesMetric> timeSeriesToWrite() {
        return ts;
    }

    public Collection<UniqueCountMetric> uniqueCountToWrite() {
        return uc;
    }

    private final Collection<SimpleCountMetric> sc;
    private final Collection<TimeSeriesMetric> ts;
    private final Collection<UniqueCountMetric> uc;
}
