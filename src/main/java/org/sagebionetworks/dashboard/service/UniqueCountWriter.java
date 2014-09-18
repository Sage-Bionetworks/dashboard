package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.parse.Record;
import org.springframework.stereotype.Service;

@Service("uniqueCountWriter")
public class UniqueCountWriter<R extends Record> extends AbstractMetricWriter<R, String> {

    public void removeValue(String userId, String metricName) {
        uniqueCountDao.removeValue(userId, nameIdDao.getId(metricName));
    }

    @Resource
    private UniqueCountDao uniqueCountDao;

    @Resource
    private NameIdDao nameIdDao;

    @Override
    void write(String metricId, String additionalKey, DateTime timestamp,
            String value) {
        uniqueCountDao.put(metricId, additionalKey, value, timestamp);
    }

    @Override
    List<Metric<R, String>> getMetrics() {
        // TODO Auto-generated method stub
        return null;
    }
}
