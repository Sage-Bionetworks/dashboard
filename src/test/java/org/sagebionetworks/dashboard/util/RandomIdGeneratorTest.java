package org.sagebionetworks.dashboard.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RandomIdGeneratorTest {
    @Test
    public void test() {
        RandomIdGenerator random = new RandomIdGenerator();
        String id1 = random.newId();
        assertNotNull(id1);
        assertEquals(4, id1.length());
        String id2 = random.newId();
        assertNotNull(id2);
        assertEquals(4, id2.length());
        assertFalse(id1.equals(id2)); // The chance of collision is near 0
        random = new RandomIdGenerator(100);
        String id3 = random.newId();
        assertNotNull(id3);
        assertEquals(100, id3.length());
        char[] chars = id3.toCharArray();
        for (char c : chars) {
            assertTrue(c > 47 && c < 58    // 0-9
                    || c > 64 && c < 91    // A-Z
                    || c > 96 && c < 123); // a-z
        }
    }
}
