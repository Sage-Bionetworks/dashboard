package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.*;

import org.junit.Test;

public class QuestionIndexReaderTest {

    @Test(expected=NullPointerException.class)
    public void nullResponse(){
        Response res = null;
        QuestionIndexReader reader = new QuestionIndexReader();
        reader.read(res);
    }

    @Test
    public void test(){
        Response res = new Response(12, true);
        QuestionIndexReader reader = new QuestionIndexReader();
        assertEquals(reader.read(res), "12");
    }
}
