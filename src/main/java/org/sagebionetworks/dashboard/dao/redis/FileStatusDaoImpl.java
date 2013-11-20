package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.Key.FILE_COMPLETED;
import static org.sagebionetworks.dashboard.dao.redis.Key.FILE_FAILED;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.FileStatusDao;
import org.sagebionetworks.dashboard.model.FileFailure;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

/**
 * Not thread-safe. Use LockDao to lock.
 */
@Repository("fileStatusDao")
public class FileStatusDaoImpl implements FileStatusDao {

    @Override
    public boolean isCompleted(String file) {
        return setOps.isMember(FILE_COMPLETED, file);
    }

    @Override
    public void setCompleted(String file) {
        if (isFailed(file)) {
            hashOps.delete(FILE_FAILED, file);
        }
        setOps.add(FILE_COMPLETED, file);
    }

    @Override
    public boolean isFailed(String file) {
        return hashOps.hasKey(FILE_FAILED, file);
    }

    @Override
    public void setFailed(String file, int lineNumber) {
        hashOps.put(FILE_FAILED, file, Integer.toString(lineNumber));
    }

    @Override
    public List<FileFailure> getFailures() {
        List<FileFailure> failures = new ArrayList<>();
        Map<String, String> map = hashOps.entries(FILE_FAILED);
        for (Entry<String, String> entry : map.entrySet()) {
            FileFailure failure = new FileFailure(
                    entry.getKey(), Integer.parseInt(entry.getValue()));
            failures.add(failure);
        }
        return failures;
    }

    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOps;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOps;
}
