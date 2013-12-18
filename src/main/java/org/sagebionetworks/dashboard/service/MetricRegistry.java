package org.sagebionetworks.dashboard.service;

import java.util.Collection;
import java.util.Collections;

import org.sagebionetworks.dashboard.metric.TimeSeriesMetric;
import org.sagebionetworks.dashboard.metric.UniqueCountMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("metricRegistry")
public class MetricRegistry {

    @Autowired
    public MetricRegistry(
            final Collection<TimeSeriesMetric> tsToWrite,
            final Collection<UniqueCountMetric> ucToWrite) {
        this.tsToWrite = Collections.unmodifiableCollection(tsToWrite);
        this.ucToWrite = Collections.unmodifiableCollection(ucToWrite);
    }

    public Collection<TimeSeriesMetric> timeSeriesToWrite() {
        return tsToWrite;
    }

    public Collection<UniqueCountMetric> uniqueCountToWrite() {
        return ucToWrite;
    }

    private final Collection<TimeSeriesMetric> tsToWrite;
    private final Collection<UniqueCountMetric> ucToWrite;
}
