package net.volangvang.terrania.play.data;

public class Answer {
    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    private String data;
    private String type;

    public Answer(String data, String type) {
        this.data = data;
        this.type = type;
    }
}
