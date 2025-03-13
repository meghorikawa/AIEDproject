package org.aiedwise2024.aiedproject;
/*A class to handle and parse the request parameters received in JSON format*/
public class RequestParams {
    private String grammar_construct;
    private String num_ques;
    private String cefr_lvl;

    public String getGrammarConstruct() {
        return grammar_construct;
    }

    public void setGrammarConstruct(String grammar_construct) {
        this.grammar_construct = grammar_construct;
    }

    public String getNumQuestions() {
        return num_ques;
    }

    public void setNumQuestions(String num_ques) {
        this.num_ques = num_ques;
    }

    public String getCefrLevel() {
        return cefr_lvl;
    }

    public void setCefrLevel(String cefr_lvl) {
        this.cefr_lvl = cefr_lvl;
    }
}
