package ua.opensvit.data.osd;

public class ProgramItem {
    public static final String JSON_NAME = "programs";
    public static final String DURATION = "duration";
    public static final String TITLE = "title";
    public static final String START = "start";
    public static final String END = "end";

    private int duration;
    private String title;
    private String start;
    private String end;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
