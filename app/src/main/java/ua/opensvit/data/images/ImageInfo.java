package ua.opensvit.data.images;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ua.opensvit.utils.ParcelUtils;

public class ImageInfo implements Parcelable{
    public static final String SUCCESS = "success";

    private boolean success;
    private final List<ImageItem> imageItems = new ArrayList<>();

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void addImageItem(ImageItem imageItem) {
        imageItems.add(imageItem);
    }

    public List<ImageItem> getUnmodifiableImages() {
        return Collections.unmodifiableList(imageItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBooleanArray(new boolean[] {success});
        ParcelUtils.writeToParcel(imageItems, dest, flags);
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel source) {
            ImageInfo res = new ImageInfo();
            boolean bArr[] = new boolean[1];
            source.readBooleanArray(bArr);
            res.setSuccess(bArr[0]);
            List<Parcelable> imageItems = ParcelUtils.readListFromParcel(source);
            for (int i = 0; i < imageItems.size(); i++) {
                res.addImageItem((ImageItem) imageItems.get(i));
            }
            return res;
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };
}
