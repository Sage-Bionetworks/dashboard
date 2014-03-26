package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.springframework.stereotype.Service;

@Service("dayCountWriter")
public class DayCountWriter extends AbstractMetricWriter<String> {

    @Override
    void write(String metricId, DateTime timestamp, String id) {
        dayCountDao.put(metricId, id, timestamp);
    }

    @Resource
    private UniqueCountDao dayCountDao;
}
