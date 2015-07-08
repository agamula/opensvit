package ua.opensvit;

import android.app.Application;
import ua.levtv.library.OpenWorldApi;

public class VideoStreamApplication extends Application {

    private static VideoStreamApplication sInstance;
    private static boolean IS_TEST = false;

    public static VideoStreamApplication getInstance() {
        return sInstance;
    }

    public static boolean isTest() {
        return IS_TEST;
    }

    public final void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    private OpenWorldApi mApi;
    private int mChannelId = 0;
    private int mIpTvServiceId;

    public int getChannelId()
    {
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
