package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

public class QuestionPassFilterTest {

    @Test(expected=NullPointerException.class)
    public void nullResponse(){
        CuResponseRecord res = null;
        QuestionPassFilter filter = new QuestionPassFilter();
        filter.matches(res);
    }

    @Test
    public void correctResponse(){
        CuResponseRecord res = new CuResponseRecord(1, 1, new DateTime(2014, 5, 20, 12, 0, 0, 0), true);
        QuestionPassFilter filter = new QuestionPassFilter();
        assertTrue(filter.matches(res));
    }

    @Test
    public void incorrectResponse(){
        CuResponseRecord res = new CuResponseRecord(1, 1, new DateTime(2014, 5, 20, 12, 0, 0, 0), false);
        QuestionPassFilter filter = new QuestionPassFilter();
        assertTrue(!filter.matches(res));
    }
}
