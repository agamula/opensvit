package ua.opensvit;

import android.app.Application;
import ua.levtv.library.LevtvDbApi;

public class VideoStreamApplication extends Application {

    private static VideoStreamApplication instance;
    private static boolean IS_TEST = false;

    public static VideoStreamApplication getInstance() {
        return instance;
    }

    public static boolean isTest() {
        return IS_TEST;
    }

    public final void onCreate() {
        super.onCreate();
        instance = this;
    }

    private LevtvDbApi apiDb;
    private int channelId = 0;
    private TvMenuPage page;
    private VodMenuPage vodPage;

    public int getChId()
    {
        return this.channelId;
    }

    public LevtvDbApi getDbApi()
    {
        return this.apiDb;
    }

    public TvMenuPage getUserPage()
    {
        return this.page;
    }

    public VodMenuPage getVodPage()
    {
        return this.vodPage;
    }

    public void setChId(int paramInt)
    {
        this.channelId = paramInt;
    }

    public void setDbApi(LevtvDbApi paramLevtvDbApi)
    {
        this.apiDb = paramLevtvDbApi;
    }

    public void setUserPage(TvMenuPage paramTvMenuPage)
    {
        this.page = paramTvMenuPage;
    }

    public void setVodPage(VodMenuPage paramVodMenuPage)
    {
        this.vodPage = paramVodMenuPage;
    }
}
