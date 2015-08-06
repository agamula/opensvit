package ua.opensvit;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.HashMap;
import java.util.Map;

import ua.opensvit.api.OpenWorldApi1;
import ua.opensvit.data.PlayerInfo;
import ua.opensvit.data.menu.TvMenuInfo;

public final class VideoStreamApp extends Application {

    private static VideoStreamApp sInstance;

    public static VideoStreamApp getInstance() {
        return sInstance;
    }

    public final void onCreate() {
        super.onCreate();
        sInstance = this;
        mRefWatcher = LeakCanary.install(this);
        initServerConfigs();
        initImageLoader(getApplicationContext());
    }

    private RefWatcher mRefWatcher;

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    private boolean mIsMac;
    private boolean mIsTest;
    private boolean mMacSet;
    private boolean mTestSet;

    private void initServerConfigs() {
        mMacSet = mTestSet = false;
    }

    public boolean isTest() {
        return mTestSet ? mIsTest : getResources().getBoolean(R.bool.is_test);
    }

    public boolean isMac() {
        return mMacSet ? mIsMac : getResources().getBoolean(R.bool.is_mac);
    }

    public void setIsMac(boolean mIsMac) {
        mMacSet = true;
        this.mIsMac = mIsMac;
    }

    public void setIsTest(boolean mIsTest) {
        mTestSet = true;
        this.mIsTest = mIsTest;
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(5)
                .threadPriority(Thread.MIN_PRIORITY + 3)
                .build();

        ImageLoader.getInstance().init(config);
    }

    private final Map<Integer, Object> mLoaderObjects = new HashMap<>();

    public void setTempLoaderObject(int loaderId, Object mTempLoaderObject) {
        mLoaderObjects.put(loaderId, mTempLoaderObject);
    }

    public Object getTempLoaderObject(int loaderId) {
        return mLoaderObjects.get(loaderId);
    }

    private OpenWorldApi1 mApi1;
    private int mChannelId = 0;
    private int mIpTvServiceId;
    private boolean mFirstNotOnline;

    public int getChannelId() {
        return this.mChannelId;
    }

    public void setChannelId(int paramInt) {
        this.mChannelId = paramInt;
    }

    public void setApi1(OpenWorldApi1 mApi1) {
        this.mApi1 = mApi1;
    }

    public OpenWorldApi1 getApi1() {
        return mApi1;
    }

    public void setIpTvServiceId(int mIpTvServiceId) {
        this.mIpTvServiceId = mIpTvServiceId;
    }

    public int getIpTvServiceId() {
        return mIpTvServiceId;
    }

    private TvMenuInfo menuInfo;

    public void setMenuInfo(TvMenuInfo menuInfo) {
        this.menuInfo = menuInfo;
    }

    public TvMenuInfo getMenuInfo() {
        return menuInfo;
    }

    public void setFirstNotOnline(boolean mFirstNotOnline) {
        this.mFirstNotOnline = mFirstNotOnline;
    }

    public boolean isFirstNotOnline() {
        return mFirstNotOnline;
    }

    private final PlayerInfo playerInfo = new PlayerInfo();

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}
