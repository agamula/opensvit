package ua.opensvit.data.epg;

import android.os.Parcel;
import android.os.Parcelable;

import ua.opensvit.utils.ParcelUtils;

public class ProgramItem implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        ParcelUtils.writeToParcel(time, dest, flags);
        ParcelUtils.writeToParcel(title, dest, flags);
        dest.writeBooleanArray(new boolean[]{isArchive});
    }

    public static final Parcelable.Creator<ProgramItem> CREATOR = new Creator<ProgramItem>() {
        @Override
        public ProgramItem createFromParcel(Parcel source) {
            ProgramItem res = new ProgramItem();
            res.setTimestamp(source.readLong());
            res.setTime(ParcelUtils.readStringFromParcel(source));
            res.setTitle(ParcelUtils.readStringFromParcel(source));
            boolean arr[] = new boolean[1];
            source.readBooleanArray(arr);
            res.setIsArchive(arr[0]);
            return res;
        }

        @Override
        public ProgramItem[] newArray(int size) {
            return new ProgramItem[size];
        }
    };
}
