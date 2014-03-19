package org.sagebionetworks.dashboard.context;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class DashboardContextTest {

    @Test
    public void test() {
        DashboardContext context = new DashboardContext();
        assertFalse(context.isProd());
        assertFalse("username".equals(context.getDwUsername()));
        assertFalse("password".equals(context.getDwPassword()));
    }
}
