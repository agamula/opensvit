package ua.opensvit.data.authorization.login_password;

import android.os.Parcel;
import android.os.Parcelable;

import ua.opensvit.data.authorization.AuthorizationInfoBase;
import ua.opensvit.utils.ParcelUtils;

public class AuthorizationInfo extends AuthorizationInfoBase implements Parcelable{

    private UserInfo userInfo;

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        ParcelUtils.writeToParcel(userInfo, dest, flags);
    }

    public static final Creator<AuthorizationInfo> CREATOR = new Creator<AuthorizationInfo>() {
        @Override
        public AuthorizationInfo createFromParcel(Parcel source) {
            AuthorizationInfoBase base = AuthorizationInfoBase.CREATOR.createFromParcel(source);
            Parcelable userInfoParcelable = ParcelUtils.readParcelableFromParcel(source);

            AuthorizationInfo authorizationInfo = new AuthorizationInfo();
            authorizationInfo.setError(base.getError());
            authorizationInfo.setIsActive(base.isActive());
            authorizationInfo.setIsAuthenticated(base.isAuthenticated());

            if(userInfoParcelable != null) {
                authorizationInfo.setUserInfo((UserInfo) userInfoParcelable);
            }
            return authorizationInfo;
        }

        @Override
        public AuthorizationInfo[] newArray(int size) {
            return new AuthorizationInfo[size];
        }
    };
}
