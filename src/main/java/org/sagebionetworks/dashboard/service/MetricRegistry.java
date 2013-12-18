package org.sagebionetworks.dashboard.service;

import java.util.Collection;
import java.util.Collections;

import org.sagebionetworks.dashboard.metric.TimeSeriesToWrite;
import org.sagebionetworks.dashboard.metric.UniqueCountToWrite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("metricRegistry")
public class MetricRegistry {

    @Autowired
    public MetricRegistry(
            final Collection<TimeSeriesToWrite> tsToWrite,
            final Collection<UniqueCountToWrite> ucToWrite) {
        this.tsToWrite = Collections.unmodifiableCollection(tsToWrite);
        this.ucToWrite = Collections.unmodifiableCollection(ucToWrite);
    }

    public Collection<TimeSeriesToWrite> timeSeriesToWrite() {
        return tsToWrite;
    }

    public Collection<UniqueCountToWrite> uniqueCountToWrite() {
        return ucToWrite;
    }

    private final Collection<TimeSeriesToWrite> tsToWrite;
    private final Collection<UniqueCountToWrite> ucToWrite;
}
