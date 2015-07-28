package ua.opensvit.data.films;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilmsInfo {

    public static final String SUCCESS = "success";
    public static final String TOTAL = "total";

    private boolean success;
    private int total;
    private final List<FilmItem> filmItems = new ArrayList<>();

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void addFilmItem(FilmItem filmItem) {
        filmItems.add(filmItem);
    }

    public List<FilmItem> getUnmodifiableFilms() {
        return Collections.unmodifiableList(filmItems);
    }
}
