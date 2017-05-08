package net.volangvang.terrania.play.data;

import java.util.List;

public class Question {
    public Question(Item question, List<Item> answers) {
        this.question = question;
        this.answers = answers;
    }

    private Item question;
    private List<Item> answers;

    public Item getQuestion() {
        return question;
    }

    public List<Item> getAnswers() {
        return answers;
    }
}
