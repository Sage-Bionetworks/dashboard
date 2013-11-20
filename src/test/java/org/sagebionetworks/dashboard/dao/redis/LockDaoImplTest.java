package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;
import org.sagebionetworks.dashboard.dao.LockDao;
import org.springframework.data.redis.core.RedisTemplate;

public class LockDaoImplTest extends AbstractRedisDaoTest {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private LockDao lockDao;

    @Test
    public void testSingleThread() {
        // Two locks
        final String lock1 = this.getClass().getSimpleName() + ".lock";
        final String lock2 = lock1 + ".2";
        // Acquire lock1
        final String etag1 = lockDao.acquire(lock1);
        assertNotNull(etag1);
        // Once lock1 is already held, it can't be acquired
        String etag = lockDao.acquire(lock1);
        assertNull(etag);
        // Acquire lock2
        final String etag2 = lockDao.acquire(lock2);
        assertNotNull(etag2);
        // Etags of the two locks shouldn't conflict with each other
        assertFalse(etag2.equals(etag1));
        // Cannot release lock1 with etag2
        boolean released = lockDao.release(lock1, etag2);
        assertFalse(released);
        // Make sure lock1 is still held
        etag = lockDao.acquire(lock1);
        assertNull(etag);
        // Release lock1
        released = lockDao.release(lock1, etag1);
        assertTrue(released);
        // Once released, lock1 can be acquired
        etag = lockDao.acquire(lock1);
        assertNotNull(etag);
        // Check lock1 expire
        Long expire = redisTemplate.getExpire(lock1, TimeUnit.MINUTES);
        assertNotNull(expire);
        // Make sure expire is at 30 minutes
        assertTrue(expire.longValue() <= 30L);
        assertTrue(expire.longValue() >= 29L);
    }

    @Test
    public void testMultiThread() throws Exception {

        // 10 tasks try to acquire lock1
        final String lock1 = this.getClass().getSimpleName() + ".multithread.lock1";
        Callable<String> task1 = new Callable<String> () {
            @Override
            public String call() throws Exception {
                return lockDao.acquire(lock1);
            }
            
        };
        List<Callable<String>> tasks1 = Collections.nCopies(10, task1);

        // 10 other tasks try to acquire lock2
        final String lock2 = this.getClass().getSimpleName() + ".multithread.lock2";
        Callable<String> task2 = new Callable<String> () {
            @Override
            public String call() throws Exception {
                return lockDao.acquire(lock2);
            }
        };
        List<Callable<String>> tasks2 = Collections.nCopies(10, task2);

        // Mix up the two lists of tasks
        List<Callable<String>> tasks = new ArrayList<>();
        tasks.addAll(tasks1);
        tasks.addAll(tasks2);
        Collections.shuffle(tasks);

        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        try {
            int acquired = 0;
            int total = 0;
            List<Future<String>> futures = threadPool.invokeAll(tasks);
            for (Future<String> future : futures) {
                total++;
                String etag = future.get(2L, TimeUnit.SECONDS);
                if (etag != null) {
                    acquired++;
                }
            }
            // 2 locks, 20 threads => exactly 2 threads should acquire the 2 locks
            assertEquals(20, total);
            assertEquals(2, acquired);
        }
        finally {
            threadPool.shutdown();
        }
    }
}
