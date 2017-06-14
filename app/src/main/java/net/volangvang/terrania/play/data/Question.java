package net.volangvang.terrania.play.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Question {
    public Question(Item question, List<Item> answers, String questionType) {
        this.question = question;
        this.answers = answers;
        this.questionType = questionType;
    }

    private Item question;
    private List<Item> answers;
    @SerializedName("question_type")
    private String questionType;
    
    public Item getQuestion() {
        return question;
    }

    public List<Item> getAnswers() {
        return answers;
    }

    public String getQuestionType() {return questionType;}
}
