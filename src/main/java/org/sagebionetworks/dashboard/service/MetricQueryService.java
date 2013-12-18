package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.TimeSeriesDao;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.model.Aggregation;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.sagebionetworks.dashboard.model.Statistic;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.springframework.stereotype.Service;

@Service("metricQueryService")
public class MetricQueryService {

    @Resource
    private TimeSeriesDao timeSeriesDao;

    @Resource
    private UniqueCountDao uniqueCountDao;

    public List<TimeDataPoint> getTimeSeries(String metricId, DateTime from, DateTime to,
            Statistic s, Aggregation a) {
        return timeSeriesDao.timeSeries(metricId, from, to, s, a);
    }

    public List<CountDataPoint> getTop(String metricId, DateTime timestamp, int n) {
        return uniqueCountDao.topCounts(metricId, timestamp, n);
    }

    public List<TimeDataPoint> getUniqueCount(String metricId, DateTime from, DateTime to) {
        return uniqueCountDao.uniqueCounts(metricId, from, to);
    }
}
