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
public class TopProjectMetricTest {

    @Resource
    private UniqueCountMetric topProjectMetric;

    @Test
    public void test() {
        assertEquals("topProjectMetric", topProjectMetric.getName());
        assertTrue(topProjectMetric.getRecordReader() instanceof BenefactorIdReader);
        assertEquals(1, topProjectMetric.getFilters().size());
        assertTrue(topProjectMetric.getFilters().get(0) instanceof ProdFilter);
    }
}
