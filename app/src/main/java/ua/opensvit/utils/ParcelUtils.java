package ua.opensvit.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

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

    public static void writeToParcel(List<? extends Parcelable> parcelables, Parcel dest, int flags) {
        if(parcelables == null || parcelables.isEmpty()) {
            dest.writeInt(0);
        } else {
            int size = parcelables.size();
            dest.writeInt(size);
            for (int i = 0; i < size; i++) {
                Parcelable p = parcelables.get(i);
                if(p == null) {
                    dest.writeInt(0);
                } else {
                    dest.writeInt(1);
                    dest.writeParcelable(p, flags);
                }
            }
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
            return source.readParcelable(Thread.currentThread().getContextClassLoader());
        } else {
            return null;
        }
    }

    public static List<Parcelable> readListFromParcel(Parcel source) {
        int size = source.readInt();
        if(size == 0) {
            return new ArrayList<>(1);
        }
        List<Parcelable> res = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            boolean isNull = source.readInt() != 1;
            if(isNull) {
                res.add(null);
            } else {
                res.add(source.readParcelable(Thread.currentThread().getContextClassLoader()));
            }
        }
        return res;
    }
}
