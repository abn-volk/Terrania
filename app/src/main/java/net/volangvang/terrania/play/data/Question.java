package net.volangvang.terrania.play.data;

import java.util.List;

public class Question {
    public Question(String question, List<Answer> answers) {
        this.question = question;
        this.answers = answers;
    }

    private String question;
    private List<Answer> answers;

    public String getQuestion() {
        return question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }
}
