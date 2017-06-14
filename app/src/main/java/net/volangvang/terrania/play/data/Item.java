package net.volangvang.terrania.play.data;

public class Item {

    private String data;
    private String type;


    /* Return the data of a question or an answer */
    public String getData() {
        return data;
    }

    /** Return data type */
    public String getType() {
        return type;
    }

    public Item(String data, String type) {
        this.data = data;
        this.type = type;
    }
}
