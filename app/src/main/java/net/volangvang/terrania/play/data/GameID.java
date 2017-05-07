package net.volangvang.terrania.play.data;

public class GameID {
    private String hashedId;
    public GameID(String hashedId) {
        this.hashedId = hashedId;
    }

    public String getHashedId() {
        return hashedId;
    }
}
