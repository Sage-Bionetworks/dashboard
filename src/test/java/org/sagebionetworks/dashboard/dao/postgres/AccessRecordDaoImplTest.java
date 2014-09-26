package org.sagebionetworks.dashboard.dao.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.dashboard.context.DashboardContext;
import org.sagebionetworks.dashboard.dao.AccessRecordDao;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.RepoRecord;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class AccessRecordDaoImplTest {

    @Resource
    private AccessRecordDao accessRecordDao;

    @Resource
    private DashboardContext dashboardContext;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(200);

    @Before
    public void before() throws Exception {
        assertNotNull(dashboardContext);
        assertNotNull(accessRecordDao);
    }

    @After
    public void cleanup() {
        accessRecordDao.cleanup();
        threadPool.shutdown();
    }

    @Test
    public void test() {
        List<Runnable> tasks = new ArrayList<Runnable>();
        List<AccessRecord> recordList = createRecordList();
        for (final AccessRecord record : recordList) {
            tasks.add(new Runnable() {

                @Override
                public void run() {
                    accessRecordDao.put(record);
                }
            });
        }
        assertEquals(recordList.size(), tasks.size());

        for (Runnable task : tasks) {
            threadPool.submit(task);
        }

        try {
            Thread.sleep(recordList.size() * 5L);
            while (!isDone()) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(recordList.size()/10, accessRecordDao.count());
    }

    private boolean isDone() {
        ThreadPoolExecutor pool = (ThreadPoolExecutor)threadPool;
        return (pool.getActiveCount() == 0 && pool.getQueue().size() == 0);
    }

    private List<AccessRecord> createRecordList() {
        List<AccessRecord> recordList = new ArrayList<>();
        // 10 000 records
        for (int i = 0; i< 10000; i++) {
            AccessRecord record = createNewRecord();
            // copy each record 10 times
            for (int j = 0; j < 10; j++) {
                recordList.add(record);
            }
        }
        return recordList;
    }

    private AccessRecord createNewRecord() {
        RepoRecord record = new RepoRecord();

        Random randomNumber = new Random();
        SecureRandom randomString = new SecureRandom();

        long elapse_ms = randomNumber.nextLong();
        record.setLatency(elapse_ms);

        long timestamp = randomNumber.nextLong();
        record.setTimestamp(timestamp);

        String thread_id = Long.toString(randomNumber.nextLong());
        record.setThreadId(thread_id);

        String session_id = UUID.randomUUID().toString();
        record.setSessionId(session_id);

        String request_url = new BigInteger(100, randomString).toString(32);
        record.setUri(request_url);

        String method = new BigInteger(10, randomString).toString(32);
        record.setMethod(method);

        String instance = Integer.toString(randomNumber.nextInt());
        record.setInstance(instance);

        String response_status = Integer.toString(randomNumber.nextInt());
        record.setStatus(response_status);
        return record;
    }

}
