package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

public class QuestionFailFilterTest {

    @Test(expected=NullPointerException.class)
    public void nullResponse(){
        CuResponseRecord res = null;
        QuestionFailFilter filter = new QuestionFailFilter();
        filter.matches(res);
    }

    @Test
    public void correctResponse(){
        CuResponseRecord res = new CuResponseRecord(1, 1, new DateTime(2014, 5, 20, 12, 0, 0, 0), true);
        QuestionFailFilter filter = new QuestionFailFilter();
        assertTrue(!filter.matches(res));
    }

    @Test
    public void incorrectResponse(){
        CuResponseRecord res = new CuResponseRecord(1, 1, new DateTime(2014, 5, 20, 12, 0, 0, 0), false);
        QuestionFailFilter filter = new QuestionFailFilter();
        assertTrue(filter.matches(res));
    }
}
