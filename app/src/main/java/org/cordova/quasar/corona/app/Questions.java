package org.cordova.quasar.corona.app;

public class Questions {

    private String question;
    private String answer;
    private boolean expanded;

    public Questions(String question, String answer){
        this.question = question;
        this.answer = answer;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }


}
