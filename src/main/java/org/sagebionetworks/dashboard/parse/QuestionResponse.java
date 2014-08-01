package org.sagebionetworks.dashboard.parse;

/**
 * The response to a question
 */
public class QuestionResponse {

    public QuestionResponse(int questionIndex, String concreteType) {
        this.questionIndex = questionIndex;
        this.concreteType = concreteType;
    }

    public int questionIndex() {
        return questionIndex;
    }
    public String concreteType(){
        return concreteType;
    }

    private int questionIndex;
    private String concreteType;
}
