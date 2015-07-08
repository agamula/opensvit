package ua.opensvit.data.iptv.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TvMenuInfo {
    public static final String SERVICE = "service";
    public static final String SUCCESS = "success";

    private final List<TvMenuItem> mTvMenuItems = new ArrayList<>();
    private int service;
    private boolean success;

    public List<TvMenuItem> getUnmodifiableTVItems() {
        return Collections.unmodifiableList(mTvMenuItems);
    }

    public void addItem(TvMenuItem item) {
        mTvMenuItems.add(item);
    }

    public void setService(int service) {
        this.service = service;
    }

    public int getService() {
        return service;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
