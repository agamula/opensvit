package ua.levtv.library;

import android.os.Parcel;
import android.os.Parcelable;

import ua.utils.ParcelUtils;

public class UserInfo implements Parcelable{
    public static final String JSON_NAME = "user";
    public static final String BALANCE = "balance";
    public static final String NAME = "name";

    private int balance;
    private String name;

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(balance);
        ParcelUtils.writeToParcel(name, dest, flags);
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            UserInfo userInfo = new UserInfo();
            userInfo.setBalance(source.readInt());
            userInfo.setName(ParcelUtils.readStringFromParcel(source));
            return userInfo;
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
