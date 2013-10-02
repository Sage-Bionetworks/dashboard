package org.sagebionetworks.dashboard.dao.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.springframework.beans.factory.annotation.Autowired;

public class NameIdDaoImplTest extends AbstractRedisDaoTest {

    @Autowired
    private NameIdDao nameIdDao;

    private final String nameIdKey = "name:id";
    private final String idNameKey = "id:name";
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
        id1 = nameIdDao.getId(name1, nameIdKey, idNameKey);
        assertNotNull(id1);
        assertTrue(id1.length() > 0);
        assertEquals(name1, nameIdDao.getName(id1, idNameKey));
        id2 = nameIdDao.getId(name2, nameIdKey, idNameKey);
        assertNotNull(id2);
        assertTrue(id2.length() > 0);
        assertFalse(id1.equals(id2));
        assertEquals(name2, nameIdDao.getName(id2, idNameKey));
    }
}
