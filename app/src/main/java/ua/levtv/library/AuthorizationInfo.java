package ua.levtv.library;

import android.os.Parcel;
import android.os.Parcelable;

import ua.utils.ParcelUtils;

public class AuthorizationInfo implements Parcelable{

    public static final String ERROR = "error";
    public static final String IS_ACTIVE = "isActive";
    public static final String IS_AUTHENTICATED = "isAuthenticated";

    private String error;
    private boolean isActive;
    private boolean isAuthenticated;
    private UserProfile userProfile;
    private UserInfo userInfo;

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(error, dest, flags);
        dest.writeBooleanArray(new boolean[]{isActive, isAuthenticated});
        ParcelUtils.writeToParcel(userInfo, dest, flags);
        ParcelUtils.writeToParcel(userProfile, dest, flags);
    }

    public static final Creator<AuthorizationInfo> CREATOR = new Creator<AuthorizationInfo>() {
        @Override
        public AuthorizationInfo createFromParcel(Parcel source) {
            AuthorizationInfo authorizationInfo = new AuthorizationInfo();
            authorizationInfo.setError(ParcelUtils.readStringFromParcel(source));
            boolean res[] = new boolean[2];
            source.readBooleanArray(res);
            authorizationInfo.setIsActive(res[0]);
            authorizationInfo.setIsAuthenticated(res[1]);
            Parcelable userInfoParcelable = ParcelUtils.readParcelableFromParcel(source);
            if(userInfoParcelable != null) {
                authorizationInfo.setUserInfo((UserInfo) userInfoParcelable);
            }
            Parcelable userProfileParcelable = ParcelUtils.readParcelableFromParcel(source);
            if(userProfileParcelable != null) {
                authorizationInfo.setUserProfile((UserProfile) userProfileParcelable);
            }
            return authorizationInfo;
        }

        @Override
        public AuthorizationInfo[] newArray(int size) {
            return new AuthorizationInfo[size];
        }
    };
}
