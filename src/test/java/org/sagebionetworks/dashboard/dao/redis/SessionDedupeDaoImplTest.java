package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Test;
import org.sagebionetworks.dashboard.dao.SessionDedupeDao;

public class SessionDedupeDaoImplTest extends AbstractRedisDaoTest {

    @Resource
    private SessionDedupeDao sessionDedupeDao;

    @Test
    public void test() {
        assertFalse(sessionDedupeDao.isProcessed("SessionDedupeDaoImplTest"));
        assertTrue(sessionDedupeDao.isProcessed("SessionDedupeDaoImplTest"));
    }
}
