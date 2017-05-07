package net.volangvang.terrania.play.data;

public class GameRequest {
    private String type;
    private String language;
    private int questionCount;
    private String continent;

    public GameRequest(String type, String language, int questionCount, String continent) {
        this.type = type;
        this.language = language;
        this.questionCount = questionCount;
        this.continent = continent;
    }


}
