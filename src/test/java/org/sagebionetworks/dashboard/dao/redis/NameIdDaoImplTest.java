package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.model.redis.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class NameIdDaoImplTest extends AbstractRedisDaoTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private NameIdDao nameIdDao;

    private final String name1 = "One Name";
    private final String name2 = "Two Name";
    private String id1;
    private String id2;

    @Before
    public void before() {
        assertNotNull(nameIdDao);
    }

    @Test
    public void test() {
        id1 = nameIdDao.getId(name1);
        assertNotNull(id1);
        assertTrue(id1.length() > 0);
        assertEquals(name1, nameIdDao.getName(id1));
        assertEquals(id1, nameIdDao.getId(name1));
        id2 = nameIdDao.getId(name2);
        assertNotNull(id2);
        assertTrue(id2.length() > 0);
        assertFalse(id1.equals(id2));
        assertEquals(name2, nameIdDao.getName(id2));
        assertEquals(id2, nameIdDao.getId(name2));
    }

    @Test
    public void testMultiThread() throws Exception {

        // Get a baseline of bootstrapped IDs
        int baseline = redisTemplate.keys("*").size();

        // Test that we can gracefully handle 200 threads
        // trying update the name-id mappings at the same time
        final int nThreads = 200;
        final Map<String, String> nameIdMap = Collections.synchronizedMap(new HashMap<String, String>());
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                String name = "Thread " + Long.toString(Thread.currentThread().getId() % 10L);
                String id = nameIdDao.getId(name);
                // Verify that once an ID is set for a name, it is never reset
                String oldId = nameIdMap.put(name, id);
                if (oldId != null && !oldId.equals(id)) {
                    fail("ID is reset.");
                }
                return id;
            }
        };
        List<Callable<String>> tasks = Collections.nCopies(nThreads, task);
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        try {
            executor.invokeAll(tasks);
            BoundHashOperations<String, String, String> nameIdHash = redisTemplate.boundHashOps(Key.NAME_ID);
            BoundHashOperations<String, String, String> idNameHash = redisTemplate.boundHashOps(Key.ID_NAME);
            Map<String, String> nameIdEntries = nameIdHash.entries();
            int i = 0;
            while (nameIdEntries.size() < nThreads && i < 5) {
                Thread.sleep(200L);
                nameIdEntries = nameIdHash.entries();
                i++;
            }
            for (Entry<String, String> en : nameIdEntries.entrySet()) {
                System.out.println(en.getKey() + "   " + en.getValue());
            }
            assertEquals(10 + baseline, nameIdEntries.size());
            Map<String, String> idNameEntries = idNameHash.entries();
            assertEquals(10 + baseline, idNameEntries.size());
            for (Entry<String, String> entry : idNameEntries.entrySet()) {
                assertNotNull(entry.getKey());
                assertFalse(entry.getKey().isEmpty());
                assertNotNull(entry.getValue());
                assertFalse(entry.getValue().isEmpty());
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
