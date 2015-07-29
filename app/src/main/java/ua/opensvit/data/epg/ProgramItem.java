package ua.opensvit.data.epg;

public class ProgramItem {
    public static final String JSON_PARENT = "items";
    public static final String JSON_NAME = "programs";
    public static final String TIMESTAMP = "timestamp";
    public static final String TIME = "time";
    public static final String TITLE = "title";
    public static final String IS_ARCHIVE = "isArchive";

    private long timestamp;
    private String time;
    private String title;
    private boolean isArchive;

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsArchive(boolean isArchive) {
        this.isArchive = isArchive;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public boolean isArchive() {
        return isArchive;
    }
}
