package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.TimeSeriesDao;
import org.springframework.stereotype.Service;

@Service("timeSeriesWriter")
public class TimeSeriesWriter extends AbstractMetricWriter<Long> {

    @Override
    void write(String metricId, DateTime timestamp, Long value) {
        timeSeriesDao.add(metricId, timestamp, value);
    }

    @Resource
    private TimeSeriesDao timeSeriesDao;
}
