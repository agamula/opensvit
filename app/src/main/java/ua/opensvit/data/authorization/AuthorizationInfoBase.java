package ua.opensvit.data.authorization;

import android.os.Parcel;
import android.os.Parcelable;

import ua.opensvit.utils.ParcelUtils;

public class AuthorizationInfoBase implements Parcelable {

    public static final String ERROR = "error";
    public static final String IS_ACTIVE = "isActive";
    public static final String IS_AUTHENTICATED = "isAuthenticated";

    private String error;
    private boolean isActive;
    private boolean isAuthenticated;
    private UserProfileBase userProfileBase;

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

    public void setUserProfileBase(UserProfileBase userProfileBase) {
        this.userProfileBase = userProfileBase;
    }

    public UserProfileBase getUserProfileBase() {
        return userProfileBase;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(error, dest, flags);
        dest.writeBooleanArray(new boolean[]{isActive, isAuthenticated});
        ParcelUtils.writeToParcel(userProfileBase, dest, flags);
    }

    public static final Creator<AuthorizationInfoBase> CREATOR = new Creator<AuthorizationInfoBase>() {
        @Override
        public AuthorizationInfoBase createFromParcel(Parcel source) {
            AuthorizationInfoBase authorizationInfo = new AuthorizationInfoBase();
            authorizationInfo.setError(ParcelUtils.readStringFromParcel(source));
            boolean res[] = new boolean[2];
            source.readBooleanArray(res);
            authorizationInfo.setIsActive(res[0]);
            authorizationInfo.setIsAuthenticated(res[1]);
            Parcelable userProfileParcelable = ParcelUtils.readParcelableFromParcel(source);
            if (userProfileParcelable != null) {
                authorizationInfo.setUserProfileBase((UserProfileBase) userProfileParcelable);
            }
            return authorizationInfo;
        }

        @Override
        public AuthorizationInfoBase[] newArray(int size) {
            return new AuthorizationInfoBase[size];
        }
    };
}