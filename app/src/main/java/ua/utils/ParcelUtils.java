package ua.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelUtils {
    private ParcelUtils() {

    }

    public static void writeToParcel(String nullString, Parcel dest, int flags) {
        if(nullString != null) {
            dest.writeInt(1);
            dest.writeString(nullString);
        } else {
            dest.writeInt(0);
        }
    }

    public static void writeToParcel(Parcelable nullParcelable, Parcel dest, int flags) {
        if(nullParcelable != null) {
            dest.writeInt(1);
            dest.writeParcelable(nullParcelable, flags);
        } else {
            dest.writeInt(0);
        }
    }

    public static String readStringFromParcel(Parcel source) {
        if(source.readInt() == 1) {
            return source.readString();
        } else {
            return null;
        }
    }

    public static Parcelable readParcelableFromParcel(Parcel source) {
        if(source.readInt() == 1) {
            return source.readParcelable(ClassLoader.getSystemClassLoader());
        } else {
            return null;
        }
    }
}
