package ua.opensvit.data.iptv.base;

public class TvMenuItem {
    public static final String JSON_NAME = "items";
    public static final String ID = "id";
    public static final String NAME = "name";

    private int id;
    private String name;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
