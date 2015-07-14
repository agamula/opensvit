package ua.opensvit.data.iptv.channels;

import android.os.Parcel;
import android.os.Parcelable;

import ua.opensvit.utils.ParcelUtils;

public class Channel implements Parcelable{

    public static final String JSON_NAME = "items";
    public static final String FAVORITS = "favorite";
    public static final String ID = "id";
    public static final String LOGO = "logo";
    public static final String NAME = "name";
    public static final String ARCHIVE = "archive";

    private boolean favorits;
    private int id;
    private String logo;
    private String name;
    private String archive;

    public boolean isFavorits() {
        return favorits;
    }

    public void setFavorits(boolean favorits) {
        this.favorits = favorits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArchive() {
        return archive;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBooleanArray(new boolean[] {favorits});
        dest.writeInt(id);
        ParcelUtils.writeToParcel(logo, dest, flags);
        ParcelUtils.writeToParcel(name, dest, flags);
        ParcelUtils.writeToParcel(archive, dest, flags);
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel source) {
            Channel res = new Channel();
            boolean arr[] = new boolean[1];
            source.readBooleanArray(arr);
            res.setFavorits(arr[0]);
            res.setId(source.readInt());
            res.setLogo(ParcelUtils.readStringFromParcel(source));
            res.setName(ParcelUtils.readStringFromParcel(source));
            res.setArchive(ParcelUtils.readStringFromParcel(source));
            return res;
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
}
