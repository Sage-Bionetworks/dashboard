package org.sagebionetworks.dashboard.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.KeyCachedDao;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.SimpleCountDao;
import org.sagebionetworks.dashboard.dao.TimeSeriesDao;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.sagebionetworks.dashboard.model.Statistic;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.sagebionetworks.dashboard.model.UserDataPoint;
import org.springframework.stereotype.Service;

@Service("metricReader")
public class MetricReader {

    @Resource
    private KeyCachedDao keyDao;

    @Resource
    private NameIdDao nameIdDao;

    @Resource
    private TimeSeriesDao timeSeriesDao;

    @Resource
    private UniqueCountDao uniqueCountDao;

    @Resource
    private SimpleCountDao simpleCountDao;

    public List<TimeDataPoint> getTimeSeries(String metricName, DateTime from, DateTime to, Statistic s, Interval a) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return timeSeriesDao.get(metricId, from, to, s, a);
    }

    public List<CountDataPoint> getTop(String metricName, Interval interval, DateTime timestamp, int offset, int size) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return uniqueCountDao.getTop(metricId, interval, timestamp, offset, size);
    }

    public List<TimeDataPoint> getUniqueCount(String metricName, String id, Interval interval, DateTime from, DateTime to) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return uniqueCountDao.getUnique(metricId + ":" + id, interval, from, to, 0L, Long.MAX_VALUE);
    }

    public List<TimeDataPoint> getUniqueCount(String metricName, Interval interval, DateTime from, DateTime to) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return uniqueCountDao.getUnique(metricId, interval, from, to, 0L, Long.MAX_VALUE);
    }

    public List<TimeDataPoint> getUniqueCount(String metricName, Interval interval, DateTime from, DateTime to, long min, long max) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return uniqueCountDao.getUnique(metricId, interval, from, to, min, max);
    }

    public List<TimeDataPoint> getCount(String metricName, String id, Interval interval, DateTime from, DateTime to) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return uniqueCountDao.get(metricId, id, interval, from, to);
    }

    public List<TimeDataPoint> getCount(String metricName, DateTime from, DateTime to) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return simpleCountDao.get(metricId, from, to);
    }

    public List<UserDataPoint> getAllReport(String metricName, String entityId) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        if (entityId.startsWith("syn")) {
            entityId = entityId.substring(3);
        }
        Set<String> keys = uniqueCountDao.getAllKeys(metricId + ":" + entityId);
        Set<UserDataPoint> res = new HashSet<UserDataPoint>();
        for (String key : keys) {
            res.addAll(convertToUserDataPoint(uniqueCountDao.getAllValues(key)));
        }
        return new ArrayList<UserDataPoint>(res);
    }

    /*
     * @param metricName (questionPassMetric or questionFailMetric)
     * @param ids (questionIndex: 0 - 29)
     * @return a map of id maps to the total unique responses found
     */
    public Map<String, String> getTotalCount(String metricName, List<String> ids) {
        metricName = getMetricName(metricName);
        Map<String, String> res = new HashMap<String, String>();
        for (String id : ids) {
            Set<String> keys = keyDao.getAllKeys(metricName, id);
            Set<String> values = new HashSet<String>();
            for (String key : keys) {
                values.addAll(uniqueCountDao.getAllValues(key));
            }
            res.put(id, Integer.toString(values.size()));
        }
        return res;
    }

    private Collection<? extends UserDataPoint> convertToUserDataPoint(
            Set<String> data) {
        List<UserDataPoint> results = new ArrayList<UserDataPoint>();
        for (String value : data) {
            //results.add(new UserDataPoint(nameIdDao.getName(value)));
            results.add(new UserDataPoint(value));
        }
        Collections.sort(results, new Comparator<UserDataPoint>() {
            @Override
            public int compare(UserDataPoint udata1, UserDataPoint udata2) {
                int res = udata1.userId().compareTo(udata2.userId());
                if (res != 0) {
                    return res;
                } else {
                    return udata1.timestamp().compareTo(udata2.timestamp());
                }
            }
        });
        return results;
    }

    private String getMetricId(String metricName) {
       metricName = getMetricName(metricName);
       if (!nameIdDao.hasName(metricName)) {
            throw new IllegalArgumentException(metricName + " is an invalid metric name or " + 
                    "metric does not have any data yet");
        }
        return nameIdDao.getId(metricName);
    }

    private String getMetricName(String metricName) {
        if(!metricName.endsWith("Metric")) {
            return metricName + "Metric";
        }
        return metricName;
    }
}
