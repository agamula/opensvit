package ua.opensvit.data;

import android.os.Parcel;
import android.os.Parcelable;

import ua.opensvit.utils.ParcelUtils;

public class ParcelableArray<T extends Parcelable> implements Parcelable {

    private final T[] values;

    public ParcelableArray(T[] values) {
        this.values = values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelableArray(values, flags);
    }

    public static final Creator<ParcelableArray<?>> CREATOR = new Creator<ParcelableArray<?>>() {
        @Override
        public ParcelableArray<?> createFromParcel(Parcel source) {
            Parcelable parcelables[] = source.readParcelableArray(ParcelUtils.getParcelReadLoader
                    ());
            ParcelableArray<?> res = new ParcelableArray<>(parcelables);
            return res;
        }

        @Override
        public ParcelableArray<?>[] newArray(int size) {
            return new ParcelableArray<?>[size];
        }
    };
}
