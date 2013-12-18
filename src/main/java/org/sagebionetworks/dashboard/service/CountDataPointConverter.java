package org.sagebionetworks.dashboard.service;

import org.sagebionetworks.dashboard.model.CountDataPoint;

public interface CountDataPointConverter {
    CountDataPoint convert(CountDataPoint dataPoint);
}
