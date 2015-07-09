package ua.opensvit.data.authorization.login_password;

import android.os.Parcel;
import android.os.Parcelable;

import ua.opensvit.data.authorization.UserProfileBase;

public class UserProfile extends UserProfileBase implements Parcelable{

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel source) {
            UserProfileBase base = UserProfileBase.CREATOR.createFromParcel(source);
            UserProfile userProfile = new UserProfile();
            userProfile.setId(base.getId());
            userProfile.setLanguage(base.getLanguage());
            userProfile.setRatio(base.getRatio());
            userProfile.setReminder(base.getReminder());
            userProfile.setResolution(base.getResolution());
            userProfile.setShowWelcome(base.isShowWelcome());
            userProfile.setSkin(base.getSkin());
            userProfile.setStartPage(base.getStartPage());
            userProfile.setTransparency(base.getTransparency());
            userProfile.setType(base.getType());
            userProfile.setVolume(base.getVolume());
            return userProfile;
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };
}
