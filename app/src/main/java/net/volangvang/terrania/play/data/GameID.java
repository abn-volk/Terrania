package net.volangvang.terrania.play.data;

public class GameID {
    private String hashed_id;
    public GameID(String hashed_id) {
        this.hashed_id = hashed_id;
    }

    /* Return the game id */
    public String getHashed_id() {
        return hashed_id;
    }
}
