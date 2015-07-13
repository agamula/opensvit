package ua.opensvit;

import android.app.Application;

import ua.opensvit.api.OpenWorldApi;

public final class VideoStreamApp extends Application {

    private static VideoStreamApp sInstance;

    public static VideoStreamApp getInstance() {
        return sInstance;
    }

    public boolean isTest() {
        return mTestSet ? mIsTest : getResources().getBoolean(R.bool.is_test);
    }

    public boolean isMac() {
        return mMacSet ? mIsMac : getResources().getBoolean(R.bool.is_mac);
    }

    public final void onCreate() {
        super.onCreate();
        sInstance = this;
        mMacSet = mTestSet = false;
    }

    private OpenWorldApi mApi;
    private int mChannelId = 0;
    private int mIpTvServiceId;
    private boolean mIsMac;
    private boolean mIsTest;
    private boolean mMacSet;
    private boolean mTestSet;

    public void setIsMac(boolean mIsMac) {
        mMacSet = true;
        this.mIsMac = mIsMac;
    }

    public void setIsTest(boolean mIsTest) {
        mTestSet = true;
        this.mIsTest = mIsTest;
    }

    public int getChannelId() {
        return this.mChannelId;
    }

    public OpenWorldApi getApi() {
        return this.mApi;
    }

    public void setChannelId(int paramInt) {
        this.mChannelId = paramInt;
    }

    public void setDbApi(OpenWorldApi paramOpenWorldApi) {
        this.mApi = paramOpenWorldApi;
    }

    public void setIpTvServiceId(int mIpTvServiceId) {
        this.mIpTvServiceId = mIpTvServiceId;
    }

    public int getIpTvServiceId() {
        return mIpTvServiceId;
    }
}
