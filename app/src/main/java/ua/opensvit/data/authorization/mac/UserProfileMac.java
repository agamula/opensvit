package ua.opensvit.data.authorization.mac;

import android.os.Parcel;
import android.os.Parcelable;

import ua.opensvit.data.authorization.UserProfileBase;
import ua.opensvit.utils.ParcelUtils;

public class UserProfileMac extends UserProfileBase implements Parcelable{

    public static final String NETWORK_PATH = "networkPath";

    private String networkPath;

    public void setNetworkPath(String networkPath) {
        networkPath = networkPath;
    }

    public String getNetworkPath() {
        return networkPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        ParcelUtils.writeToParcel(networkPath, dest, flags);
    }

    public static final Creator<UserProfileMac> CREATOR = new Creator<UserProfileMac>() {
        @Override
        public UserProfileMac createFromParcel(Parcel source) {
            UserProfileBase base = UserProfileBase.CREATOR.createFromParcel(source);
            String networkPath = ParcelUtils.readStringFromParcel(source);
            UserProfileMac userProfileMac = new UserProfileMac();
            userProfileMac.setId(base.getId());
            userProfileMac.setLanguage(base.getLanguage());
            userProfileMac.setRatio(base.getRatio());
            userProfileMac.setReminder(base.getReminder());
            userProfileMac.setResolution(base.getResolution());
            userProfileMac.setShowWelcome(base.isShowWelcome());
            userProfileMac.setSkin(base.getSkin());
            userProfileMac.setStartPage(base.getStartPage());
            userProfileMac.setTransparency(base.getTransparency());
            userProfileMac.setType(base.getType());
            userProfileMac.setVolume(base.getVolume());
            userProfileMac.setNetworkPath(networkPath);
            return userProfileMac;
        }

        @Override
        public UserProfileMac[] newArray(int size) {
            return new UserProfileMac[size];
        }
    };
}
