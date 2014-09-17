package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.parse.Record;
import org.springframework.stereotype.Service;

@Service("uniqueCountWriter")
public class UniqueCountWriter<R extends Record> extends AbstractMetricWriter<R, String> {

    @Override
    void write(String metricId, DateTime timestamp, String id) {
        uniqueCountDao.put(metricId, id, timestamp);
    }

    public void removeValue(String userId, String metricName) {
        uniqueCountDao.removeValue(userId, nameIdDao.getId(metricName));
    }

    @Resource
    private UniqueCountDao uniqueCountDao;

    @Resource
    private NameIdDao nameIdDao;
}
