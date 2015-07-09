package ua.opensvit.data.authorization.mac;

import android.os.Parcel;
import android.os.Parcelable;

import ua.opensvit.data.authorization.AuthorizationInfoBase;
import ua.opensvit.utils.ParcelUtils;

public class AuthorizationInfoMac extends AuthorizationInfoBase implements Parcelable {

    public static final String J_SESSION = "jSession";

    private String jSession;

    public void setSession(String jSession) {
        this.jSession = jSession;
    }

    public String getSession() {
        return jSession;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        ParcelUtils.writeToParcel(jSession, dest, flags);
    }

    public static final Creator<AuthorizationInfoMac> CREATOR = new Creator<AuthorizationInfoMac>() {
        @Override
        public AuthorizationInfoMac createFromParcel(Parcel source) {
            AuthorizationInfoBase base = AuthorizationInfoBase.CREATOR.createFromParcel(source);

            AuthorizationInfoMac authorizationInfoMac = new AuthorizationInfoMac();
            authorizationInfoMac.setError(base.getError());
            authorizationInfoMac.setIsActive(base.isActive());
            authorizationInfoMac.setIsAuthenticated(base.isAuthenticated());
            authorizationInfoMac.setSession(ParcelUtils.readStringFromParcel(source));

            return authorizationInfoMac;
        }

        @Override
        public AuthorizationInfoMac[] newArray(int size) {
            return new AuthorizationInfoMac[size];
        }
    };
}
