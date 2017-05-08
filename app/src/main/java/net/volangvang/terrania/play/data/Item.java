package net.volangvang.terrania.play.data;

public class Item {
    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    private String data;
    private String type;

    public Item(String data, String type) {
        this.data = data;
        this.type = type;
    }
}
