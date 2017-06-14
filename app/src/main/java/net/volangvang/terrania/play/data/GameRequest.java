package net.volangvang.terrania.play.data;

public class GameRequest {
    private String type;
    private String language;
    private int question_count;
    private String continent;


    /* Create a game with information */
    public GameRequest(String type, String language, int question_count, String continent) {
        this.type = type;
        this.language = language;
        this.question_count = question_count;
        this.continent = continent;
    }


}
