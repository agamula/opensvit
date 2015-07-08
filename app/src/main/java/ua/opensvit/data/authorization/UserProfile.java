package ua.opensvit.data.authorization;

import android.os.Parcel;
import android.os.Parcelable;

import ua.opensvit.utils.ParcelUtils;

public class UserProfile implements Parcelable {
    public static final String JSON_NAME = "profile";
    public static final String ID = "id";
    public static final String LANGUAGE = "language";
    public static final String RATIO = "ratio";
    public static final String REMINDER = "reminder";
    public static final String RESOLUTION = "resolution";
    public static final String SHOW_WELCOME = "showWelcome";
    public static final String SKIN = "skin";
    public static final String START_PAGE = "startPage";
    public static final String TRANSPARENCY = "transparency";
    public static final String TYPE = "type";
    public static final String VOLUME = "volume";

    private int id;
    private String language;
    private String ratio;
    private int reminder;
    private String resolution;
    private boolean showWelcome;
    private String skin;
    private String startPage;
    private int transparency;
    private String type;
    private int volume;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public int getReminder() {
        return reminder;
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public boolean isShowWelcome() {
        return showWelcome;
    }

    public void setShowWelcome(boolean showWelcome) {
        this.showWelcome = showWelcome;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(String startPage) {
        this.startPage = startPage;
    }

    public int getTransparency() {
        return transparency;
    }

    public void setTransparency(int transparency) {
        this.transparency = transparency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        ParcelUtils.writeToParcel(language, dest, flags);
        ParcelUtils.writeToParcel(ratio, dest, flags);
        dest.writeInt(reminder);
        ParcelUtils.writeToParcel(resolution, dest, flags);
        dest.writeBooleanArray(new boolean[]{showWelcome});
        ParcelUtils.writeToParcel(skin, dest, flags);
        ParcelUtils.writeToParcel(startPage, dest, flags);
        dest.writeInt(transparency);
        ParcelUtils.writeToParcel(type, dest, flags);
        dest.writeInt(volume);
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel source) {
            UserProfile userProfile = new UserProfile();
            userProfile.setId(source.readInt());
            userProfile.setLanguage(ParcelUtils.readStringFromParcel(source));
            userProfile.setRatio(ParcelUtils.readStringFromParcel(source));
            userProfile.setReminder(source.readInt());
            userProfile.setResolution(ParcelUtils.readStringFromParcel(source));
            boolean vals[] = new boolean[1];
            source.readBooleanArray(vals);
            userProfile.setShowWelcome(vals[0]);
            userProfile.setSkin(ParcelUtils.readStringFromParcel(source));
            userProfile.setStartPage(ParcelUtils.readStringFromParcel(source));
            userProfile.setTransparency(source.readInt());
            userProfile.setType(ParcelUtils.readStringFromParcel(source));
            userProfile.setVolume(source.readInt());
            return userProfile;
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };
}
