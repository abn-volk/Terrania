package net.volangvang.terrania.play.data;

public class Answer {
    public String getChoice() {
        return choice;
    }

    public String getType() {
        return type;
    }

    private String choice;
    private String type;

    public Answer(String choice, String type) {
        this.choice = choice;
        this.type = type;
    }
}
