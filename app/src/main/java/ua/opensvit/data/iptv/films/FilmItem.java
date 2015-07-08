package ua.opensvit.data.iptv.films;

public class FilmItem {
    public static final String JSON_NAME = "items";
    public static final String GENRE = "genre";
    public static final String ID = "id";
    public static final String LOGO = "logo";
    public static final String NAME = "name";
    public static final String ORIGIN = "origin";
    public static final String YEAR = "year";

    private String genre;
    private int id;
    private String logo;
    private String name;
    private String origin;
    private int year;

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo() {
        return logo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOrigin() {
        return origin;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }
}
