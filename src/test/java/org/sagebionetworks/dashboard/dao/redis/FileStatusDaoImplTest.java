package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.sagebionetworks.dashboard.dao.FileStatusDao;
import org.sagebionetworks.dashboard.model.FileFailure;
import org.springframework.data.redis.core.RedisTemplate;

public class FileStatusDaoImplTest extends AbstractRedisDaoTest {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private FileStatusDao fileStatusDao;

    @Test
    public void test() {

        // file1 completed
        String file1 = getClass().getSimpleName() + ".file1";
        assertFalse(fileStatusDao.isCompleted(file1));
        fileStatusDao.setCompleted(file1);
        assertTrue(fileStatusDao.isCompleted(file1));
        assertFalse(fileStatusDao.isFailed(file1));
        List<FileFailure> failures = fileStatusDao.getFailures();
        assertNotNull(failures);
        assertEquals(0, failures.size());

        // file2 failed
        String file2 = getClass().getSimpleName() + ".file2";
        assertFalse(fileStatusDao.isFailed(file2));
        fileStatusDao.setFailed(file2, 2);
        assertTrue(fileStatusDao.isFailed(file2));
        assertFalse(fileStatusDao.isCompleted(file2));
        failures = fileStatusDao.getFailures();
        assertNotNull(failures);
        assertEquals(1, failures.size());
        assertEquals(file2, failures.get(0).getFile());
        assertEquals(2, failures.get(0).getLineNumber());

        // file3 failed
        String file3 = getClass().getSimpleName() + ".file3";
        assertFalse(fileStatusDao.isFailed(file3));
        fileStatusDao.setFailed(file3, 3);
        assertTrue(fileStatusDao.isFailed(file3));
        assertFalse(fileStatusDao.isCompleted(file3));
        failures = fileStatusDao.getFailures();
        assertNotNull(failures);
        assertEquals(2, failures.size());

        // Mark file2 as completed
        fileStatusDao.setCompleted(file2);
        assertTrue(fileStatusDao.isCompleted(file2));
        assertFalse(fileStatusDao.isFailed(file2));
        // Also check file1
        assertTrue(fileStatusDao.isCompleted(file1));
        assertFalse(fileStatusDao.isFailed(file1));
        // Also check file3
        assertFalse(fileStatusDao.isCompleted(file3));
        assertTrue(fileStatusDao.isFailed(file3));
        failures = fileStatusDao.getFailures();
        assertNotNull(failures);
        assertEquals(1, failures.size());
        assertEquals(file3, failures.get(0).getFile());
        assertEquals(3, failures.get(0).getLineNumber());
    }
}
