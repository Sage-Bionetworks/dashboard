package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:/META-INF/spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TopProjectByDayMetricTest {

    @Resource
    private DayCountMetric topProjectByDayMetric;

    @Test
    public void test() {
        assertEquals("topProjectByDayMetric", topProjectByDayMetric.getName());
        assertTrue(topProjectByDayMetric.getRecordReader() instanceof ProjectIdReader);
        assertEquals(2, topProjectByDayMetric.getFilters().size());
        assertTrue(topProjectByDayMetric.getFilters().get(0) instanceof ProdFilter);
    }

}
