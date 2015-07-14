package ua.opensvit.data.iptv.channels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ua.opensvit.utils.ParcelUtils;

public class ChannelsInfo implements Parcelable{
    public static final String SUCCESS = "success";
    public static final String TOTAL = "total";

    private boolean success;
    private int total;
    private final List<Channel> channels = new ArrayList<>();

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public List<Channel> getUnmodifiableChannels() {
        return Collections.unmodifiableList(channels);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBooleanArray(new boolean[]{success});
        dest.writeInt(total);

        ParcelUtils.writeToParcel(channels, dest, flags);
    }

    public static final Creator<ChannelsInfo> CREATOR = new Creator<ChannelsInfo>() {
        @Override
        public ChannelsInfo createFromParcel(Parcel source) {
            ChannelsInfo res = new ChannelsInfo();
            boolean arr[] = new boolean[1];
            source.readBooleanArray(arr);
            res.setSuccess(arr[0]);
            res.setTotal(source.readInt());
            List<Parcelable> channels = ParcelUtils.readListFromParcel(source);
            for (int i = 0; i < channels.size(); i++) {
                res.addChannel((Channel) channels.get(i));
            }
            return res;
        }

        @Override
        public ChannelsInfo[] newArray(int size) {
            return new ChannelsInfo[size];
        }
    };
}
