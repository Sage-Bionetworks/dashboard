package org.sagebionetworks.dashboard.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
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

    @Resource
    private NameIdDao nameIdDao;

    public List<TimeDataPoint> getTimeSeries(
            String metricId, DateTime from, DateTime to, Statistic s, Aggregation a) {
        return timeSeriesDao.timeSeries(metricId, from, to, s, a);
    }

    public List<CountDataPoint> getTop25(String metricId, DateTime timestamp) {
        return uniqueCountDao.topCounts(metricId, timestamp, 25);
    }

    public List<CountDataPoint> getTop25(String metricId, DateTime timestamp,
            CountDataPointConverter converter) {
        List<CountDataPoint> results = getTop25(metricId, timestamp);
        for (int i = 0; i < results.size(); i++) {
            results.set(i, converter.convert(results.get(i)));
        }
        return results;
    }

    public List<TimeDataPoint> getUniqueCount(String metricId, DateTime from, DateTime to) {
        return uniqueCountDao.uniqueCounts(metricId, from, to);
    }

    public List<List<TimeDataPoint>> getUniqueCount(List<String> metricIds,
            DateTime from, DateTime to) {
        List<List<TimeDataPoint>> results = new ArrayList<>(metricIds.size());
        for (String metricId : metricIds) {
            results.add(uniqueCountDao.uniqueCounts(metricId, from, to));
        }
        return results;
    }
}
