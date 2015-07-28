package ua.opensvit.data;

public class CreepingLineItem {
    public static final String TEXT = "text";
    public static final String SUCCESS = "success";

    private String text;
    private boolean success;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
