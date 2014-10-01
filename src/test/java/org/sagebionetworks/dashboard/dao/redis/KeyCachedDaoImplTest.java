package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.dashboard.dao.KeyCachedDao;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.parse.CuResponseRecord;
import org.springframework.data.redis.core.RedisTemplate;

public class KeyCachedDaoImplTest extends AbstractRedisDaoTest{

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private NameIdDao nameIdDao;

    @Resource
    private KeyCachedDao keyDao;

    private static final String passMetric = "questionPassMetric";
    private static final String failMetric = "questionFailMetric";
    private static final String prefix = "n:month:uniquecount:";
    private static final CuResponseRecord pass1 = new CuResponseRecord(1, 1, new DateTime(1403827200000L), true);
    private static final CuResponseRecord pass2 = new CuResponseRecord(12, 1, new DateTime(1403827200001L), true);
    private static final CuResponseRecord fail1 = new CuResponseRecord(1, 2, new DateTime(1403827200000L), false);
    private static final CuResponseRecord fail2 = new CuResponseRecord(12, 2, new DateTime(153827200001L), false);

    @Before
    public void before() {
        assertNotNull(nameIdDao);
        assertNotNull(keyDao);
    }

    @Test
    public void test() {
        String passMetricId = nameIdDao.getId(passMetric);
        String failMetricId = nameIdDao.getId(failMetric);

        assertEquals(keyDao.getAllKeys(passMetric, "1").size(), 0);
        assertEquals(keyDao.getAllKeys(passMetric, "10").size(), 0);
        assertEquals(keyDao.getAllKeys(failMetric, "2").size(), 0);
        assertEquals(keyDao.getAllKeys(failMetric, "22").size(), 0);
        assertEquals(keyDao.getAllKeys(passMetric, "6").size(), 0);

        keyDao.put(pass1, passMetric);
        assertEquals(keyDao.getAllKeys(passMetric, "1").size(), 1);
        assertEquals(new ArrayList<String>(keyDao.getAllKeys(passMetric, "1")),
                Arrays.asList(prefix + passMetricId + ":1:1401580800"));
        keyDao.put(pass1, passMetric);
        assertEquals(keyDao.getAllKeys(passMetric, "1").size(), 1);

        keyDao.put(fail1, failMetric);
        assertEquals(keyDao.getAllKeys(failMetric, "2").size(), 1);
        assertEquals(new ArrayList<String>(keyDao.getAllKeys(failMetric, "2")),
                Arrays.asList(prefix + failMetricId + ":2:1401580800"));
        keyDao.put(fail1, failMetric);
        assertEquals(keyDao.getAllKeys(failMetric, "2").size(), 1);

        keyDao.put(pass2, passMetric);
        assertEquals(keyDao.getAllKeys(passMetric, "1").size(), 1);
        keyDao.put(fail2, failMetric);
        assertEquals(keyDao.getAllKeys(failMetric, "2").size(), 2);
    }

}
