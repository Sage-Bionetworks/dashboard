package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.springframework.stereotype.Service;

@Service("uniqueCountWriter")
public class UniqueCountWriter extends AbstractMetricWriter<String> {

    @Override
    void write(String metricId, DateTime timestamp, String id) {
        uniqueCountDao.put(metricId, id, timestamp);
    }

    @Resource
    private UniqueCountDao uniqueCountDao;
}
