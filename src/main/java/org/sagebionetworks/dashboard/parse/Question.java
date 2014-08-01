package org.sagebionetworks.dashboard.parse;

/**
 * This class represents a single question in the Certified User Quiz.
 */
// TODO: use factory or another pattern design to refactor this class
public class Question {

    /**
     * @param prompt - the user readable prompt for this question 
     * @param reference - reference to a Wiki Page
     */
    public Question(int questionIndex, String concreteType, String prompt, String reference) {
        this.questionIndex = questionIndex;
        this.concreteType = concreteType;
        this.prompt = prompt;
        this.reference = reference;
    }

    public int questionIndex() {
        return questionIndex;
    }
    public String concreteType() {
        return concreteType;
    }
    public String prompt() {
        return prompt;
    }
    public String reference() {
        return reference;
    }

    private int questionIndex;
    private String concreteType;
    private String prompt;
    private String reference;
}
