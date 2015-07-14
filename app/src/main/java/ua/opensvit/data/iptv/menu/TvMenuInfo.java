package ua.opensvit.data.iptv.menu;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ua.opensvit.utils.ParcelUtils;

public class TvMenuInfo implements Parcelable{
    public static final String SERVICE = "service";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    private final List<TvMenuItem> mTvMenuItems = new ArrayList<>();
    private int service;
    private boolean success;
    private String error;

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

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        int size = mTvMenuItems.size();
        dest.writeInt(size);

        for (int i = 0; i < size; i++) {
            ParcelUtils.writeToParcel(mTvMenuItems.get(i), dest, flags);
        }
        dest.writeInt(service);
        dest.writeBooleanArray(new boolean[]{success});
        ParcelUtils.writeToParcel(error, dest, flags);
    }

    public static final Creator<TvMenuInfo> CREATOR = new Creator<TvMenuInfo>() {
        @Override
        public TvMenuInfo createFromParcel(Parcel source) {
            TvMenuInfo res = new TvMenuInfo();
            int size = source.readInt();
            for (int i = 0; i < size; i++) {
                Parcelable parcelable = ParcelUtils.readParcelableFromParcel(source);
                if(parcelable != null && parcelable instanceof TvMenuItem) {
                    res.addItem((TvMenuItem) parcelable);
                }
            }
            res.setService(source.readInt());
            boolean successArr[] = new boolean[1];
            source.readBooleanArray(successArr);
            res.setSuccess(successArr[0]);
            res.setError(ParcelUtils.readStringFromParcel(source));
            return res;
        }

        @Override
        public TvMenuInfo[] newArray(int size) {
            return new TvMenuInfo[size];
        }
    };
}
