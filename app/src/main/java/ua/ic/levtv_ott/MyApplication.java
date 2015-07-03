package ua.ic.levtv_ott;

import android.app.Application;
import ua.ic.levtv.library.LevtvDbApi;

public class MyApplication
        extends Application
{
    private static MyApplication singleton;
    private LevtvDbApi apiDb;
    private int channelId = 0;
    private TvMenuPage page;
    private VodMenuPage vodPage;

    public MyApplication() {}

    public static MyApplication getInstance()
    {
        return singleton;
    }

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

    public final void onCreate()
    {
        super.onCreate();
        singleton = this;
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
