package ua.opensvit.data.iptv.base;

import android.os.Parcel;
import android.os.Parcelable;

import ua.opensvit.utils.ParcelUtils;

public class TvMenuItem implements Parcelable{
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        ParcelUtils.writeToParcel(name, dest, flags);
    }

    public static final Creator<TvMenuItem> CREATOR = new Creator<TvMenuItem>() {
        @Override
        public TvMenuItem createFromParcel(Parcel source) {
            TvMenuItem res = new TvMenuItem();
            res.setId(source.readInt());
            res.setName(ParcelUtils.readStringFromParcel(source));
            return res;
        }

        @Override
        public TvMenuItem[] newArray(int size) {
            return new TvMenuItem[size];
        }
    };
}
