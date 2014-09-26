package org.sagebionetworks.dashboard.dao.postgres;

import static org.junit.Assert.assertEquals;

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
import org.junit.Test;
import org.sagebionetworks.dashboard.dao.AccessRecordDao;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.RepoRecord;

public class AccessRecordDaoImplTest {

    @Resource
    private AccessRecordDao accessRecordDao;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(200);

    @After
    public void cleanup() {
        try {
            accessRecordDao.cleanup();
        } catch (Throwable e) {
            e.printStackTrace();
        }
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

        try {
            assertEquals(recordList.size()/10, accessRecordDao.count());
        } catch (Throwable e) {
            e.printStackTrace();
        }
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
