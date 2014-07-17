package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.NameSpace.fdownload;
import static org.sagebionetworks.dashboard.dao.redis.RedisConstants.EXPIRE_DAYS;
import static org.sagebionetworks.dashboard.model.Interval.day;
import static org.sagebionetworks.dashboard.model.Interval.month;
import static org.sagebionetworks.dashboard.model.Interval.week;
import static org.sagebionetworks.dashboard.model.Statistic.n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.FileDownloadInvDao;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.UserDataPoint;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

@Repository("fileDownloadInvDao")
public class FileDownloadInvDaoImpl implements FileDownloadInvDao{

    @Override
    public void put(String metricId, String entityId, DateTime timestamp,
            Interval interval, String userData) {
        String userDataId = nameIdDao.getId(userData);
        put(metricId, entityId, userDataId, interval, timestamp);
    }

    @Override
    public List<UserDataPoint> getTop(String metricId, String entityId,
            DateTime timestamp, Interval interval, long offset, long size) {
        final String key = getKey(metricId, entityId, interval, timestamp);
        Collection<TypedTuple<String>> data = 
                zsetOps.reverseRangeWithScores(key, offset, offset + size - 1);
        List<UserDataPoint> results = new ArrayList<UserDataPoint>();
        for (TypedTuple<String> tuple : data) {
            results.add(new UserDataPoint(
                    nameIdDao.getName(tuple.getValue()), tuple.getScore().longValue()));
        }
        return Collections.unmodifiableList(results);
    }

    @Resource
    private NameIdDao nameIdDao;

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource(name="redisTemplate")
    private ZSetOperations<String, String> zsetOps;

    private void put(String metricId, String entityId, String userDataId, 
            Interval interval, DateTime timestamp) {
        String key = getKey(metricId, entityId, interval, timestamp);
        zsetOps.incrementScore(key, userDataId, 1.0d);
        Date expireAt = DateTime.now().plusDays(EXPIRE_DAYS).toDate();
        redisTemplate.expireAt(key, expireAt);
    }

    private String getKey(String metricId, String entityId, Interval interval, 
            DateTime timestamp) {
        String id = metricId + Key.SEPARATOR + entityId;
        switch (interval) {
            case day:
                return new KeyAssembler(n, day, fdownload).getKey(id, timestamp);
            case week:
                return new KeyAssembler(n, week, fdownload).getKey(id, timestamp);
            case month:
                return new KeyAssembler(n, month, fdownload).getKey(id, timestamp);
            default:
                throw new IllegalArgumentException("Interval " + interval + " is not supported.");
        }
    }
}
