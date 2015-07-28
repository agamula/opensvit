package ua.opensvit.data;

public class GetUrlItem {
    public static final String HAS_INFO_LINE = "hasInfoLine";
    public static final String SUCCESS = "success";
    public static final String URL = "url";
    public static final String IP = "ip";

    private boolean hasInfoLine;
    private boolean success;
    private String url;

    public boolean hasInfoLine() {
        return hasInfoLine;
    }

    public void setHasInfoLine(boolean hasInfoLine) {
        this.hasInfoLine = hasInfoLine;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
