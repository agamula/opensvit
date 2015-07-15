package ua.opensvit.api;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.data.constants.ApiConstants;
import ua.opensvit.data.authorization.AuthorizationInfoBase;
import ua.opensvit.data.authorization.UserProfileBase;
import ua.opensvit.data.authorization.login_password.AuthorizationInfo;
import ua.opensvit.data.authorization.login_password.UserInfo;
import ua.opensvit.data.authorization.login_password.UserProfile;
import ua.opensvit.data.authorization.mac.AuthorizationInfoMac;
import ua.opensvit.data.authorization.mac.UserProfileMac;
import ua.opensvit.data.iptv.channels.Channel;
import ua.opensvit.data.iptv.channels.ChannelsInfo;
import ua.opensvit.data.iptv.films.FilmItem;
import ua.opensvit.data.iptv.films.FilmsInfo;
import ua.opensvit.data.iptv.menu.TvMenuInfo;
import ua.opensvit.data.iptv.menu.TvMenuItem;
import ua.opensvit.http.IOkHttpLoadInfo;
import ua.opensvit.http.OkHttpAsyncTask;
import ua.opensvit.http.OkHttpClientRunnable;
import ua.opensvit.utils.ApiUtils;

public class OpenWorldApi1 {

    private boolean parseAuthorizationInfoBase(AuthorizationInfoBase res, JSONObject jsonObj) {
        boolean isAuthenticated = false;
        try {
            if (jsonObj.has(AuthorizationInfoBase.ERROR)) {
                res.setError(jsonObj.getString(AuthorizationInfoBase.ERROR));
            } else if (jsonObj.getBoolean(AuthorizationInfoBase.IS_AUTHENTICATED)) {
                res.setIsAuthenticated(true);
                isAuthenticated = true;
                if (jsonObj.getBoolean(AuthorizationInfoBase.IS_ACTIVE)) {
                    res.setIsActive(true);
                }
            }

            if (isAuthenticated) {
                JSONObject userProfileObj = jsonObj.getJSONObject(UserProfileBase.JSON_NAME);
                UserProfileBase userProfileBase = res.getUserProfileBase();
                userProfileBase.setTransparency(userProfileObj.getInt(UserProfileBase.TRANSPARENCY));
                userProfileBase.setId(userProfileObj.getInt(UserProfileBase.ID));
                userProfileBase.setReminder(userProfileObj.getInt(UserProfileBase.REMINDER));
                userProfileBase.setVolume(userProfileObj.getInt(UserProfileBase.VOLUME));
                userProfileBase.setRatio(userProfileObj.getString(UserProfileBase.RATIO));
                userProfileBase.setResolution(userProfileObj.getString(UserProfileBase.RESOLUTION));
                userProfileBase.setLanguage(userProfileObj.getString(UserProfileBase.LANGUAGE));
                userProfileBase.setStartPage(userProfileObj.getString(UserProfileBase.START_PAGE));
                userProfileBase.setType(userProfileObj.getString(UserProfileBase.TYPE));
                userProfileBase.setSkin(userProfileObj.getString(UserProfileBase.SKIN));
                userProfileBase.setShowWelcome(userProfileObj.getBoolean(UserProfileBase
                        .SHOW_WELCOME));
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void parseAuthorizationInfoMac(ProgressBar mProgress, final AuthorizationInfoMac res, String
            url, final ResultListener mListener, String... params) throws IOException {
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        for (int i = 0; i < params.length; i += 2) {
            loadInfo.addParam(params[i], params[i + 1]);
        }
        executeHttpTask1(mProgress, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    res.setUserProfileBase(new UserProfileMac());
                    if (!parseAuthorizationInfoBase(res, jsonObj)) {
                        res.setUserProfileBase(null);
                        if (mListener != null) {
                            mListener.onResult(res);
                        }
                        return;
                    }

                    res.setSession(jsonObj.getString(AuthorizationInfoMac.J_SESSION));
                    JSONObject userProfileObj = jsonObj.getJSONObject(UserProfileMac.JSON_NAME);
                    UserProfileMac userProfileMac = (UserProfileMac) res.getUserProfileBase();
                    if (userProfileObj.has(UserProfileMac.NETWORK_PATH)) {
                        userProfileMac.setNetworkPath(userProfileObj.getString(UserProfileMac
                                .NETWORK_PATH));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (mListener != null) {
                    mListener.onResult(res);
                }
            }

            @Override
            public void onLoadError(String errMsg) {
                if (mListener != null) {
                    mListener.onError(errMsg);
                }
            }
        });
    }

    public interface ResultListener {
        void onResult(Object res);
        void onError(String result);
    }

    public void macAuth(ProgressBar mProgress, ResultListener mListener)
            throws IOException {
        WifiManager manager = (WifiManager) VideoStreamApp.getInstance()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        if (info != null) {
            String mac = info.getMacAddress();
            String sn = Build.SERIAL;
            String url = ApiUtils.getApiUrl(ApiConstants.MacAddressAuth.Auth.URL);

            parseAuthorizationInfoMac(mProgress, new AuthorizationInfoMac(), url, mListener,
                    ApiConstants.MacAddressAuth.Auth.PARAM_MAC, mac, ApiConstants.MacAddressAuth
                            .Auth.PARAM_SN, sn);
        } else {
            VideoStreamApp app = VideoStreamApp.getInstance();
            Toast.makeText(app.getApplicationContext(), app.getString(R.string
                    .load_failed_message), Toast.LENGTH_SHORT).show();
        }
    }

    public void macAuth(Fragment fragment, String login, String password,
                        ResultListener mListener) throws IOException {
        WifiManager manager = (WifiManager) VideoStreamApp.getInstance()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        if (info != null) {
            String mac = info.getMacAddress();
            String sn = Build.SERIAL;
            String url = ApiUtils.getApiUrl(ApiConstants.MacAddressAuth.Auth.URL);

            parseAuthorizationInfoMac((ProgressBar) fragment.getActivity().findViewById(R.id
                    .progress), new AuthorizationInfoMac(), url, mListener, ApiConstants
                    .MacAddressAuth.Auth.PARAM_MAC, mac, ApiConstants.MacAddressAuth
                    .Auth.PARAM_SN, sn, ApiConstants.MacAddressAuth.Auth.LoginPassword
                    .PARAM_LOGIN, login, ApiConstants.MacAddressAuth.Auth.LoginPassword
                    .PARAM_PASSWORD, password);
        } else {
            VideoStreamApp app = VideoStreamApp.getInstance();
            Toast.makeText(app.getApplicationContext(), app.getString(R.string
                    .load_failed_message), Toast.LENGTH_SHORT).show();
        }
    }

    public void auth(Fragment fragment, String login, String password,
                     final ResultListener mListener) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.LoginPasswordAuth.Auth.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.LoginPasswordAuth.Auth.PARAM_LOGIN, login);
        loadInfo.addParam(ApiConstants.LoginPasswordAuth.Auth.PARAM_PASSWORD, password);
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                AuthorizationInfo res = new AuthorizationInfo();
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    res.setUserProfileBase(new UserProfile());

                    if (!parseAuthorizationInfoBase(res, jsonObj)) {
                        res.setUserProfileBase(null);
                        if (mListener != null) {
                            mListener.onResult(res);
                        }
                        return;
                    }
                    JSONObject userInfoObj = jsonObj.getJSONObject(UserInfo.JSON_NAME);
                    UserInfo userInfo = new UserInfo();
                    userInfo.setBalance(userInfoObj.getInt(UserInfo.BALANCE));
                    userInfo.setName(userInfoObj.getString(UserInfo.NAME));
                    res.setUserInfo(userInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (mListener != null) {
                    mListener.onResult(res);
                }
            }

            @Override
            public void onLoadError(String errMsg) {
                if (mListener != null) {
                    mListener.onError(errMsg);
                }
            }
        });
    }

    private void executeHttpTask(Fragment fragment, String url, IOkHttpLoadInfo.GetLoaderCreateInfo
            loadInfo, OkHttpAsyncTask.OnLoadFinishedListener mLoadFinishedListener) {
        final ProgressBar progressBar;
        if(fragment == null) {
            progressBar = null;
        } else {
            progressBar = (ProgressBar) fragment.getActivity().findViewById(R.id.progress);
        }
        executeHttpTask1(progressBar, url, loadInfo, mLoadFinishedListener);
    }

    private void executeHttpTask1(ProgressBar progressBar, String url, IOkHttpLoadInfo
            .GetLoaderCreateInfo loadInfo, OkHttpAsyncTask.OnLoadFinishedListener
            mLoadFinishedListener) {
        OkHttpClientRunnable mRunnable = new OkHttpClientRunnable(url, loadInfo);
        OkHttpAsyncTask task = new OkHttpAsyncTask(progressBar, mRunnable);
        task.setOnLoadFinishedListener(mLoadFinishedListener);
        task.execute();
    }

    private TvMenuInfo parseJsonTvMenuInfo(String tvInfoJsonString) {
        TvMenuInfo res = new TvMenuInfo();
        try {
            JSONObject jsonObj = new JSONObject(tvInfoJsonString);
            if (jsonObj.has(TvMenuInfo.SUCCESS)) {
                boolean isSuccess = jsonObj.getBoolean(TvMenuInfo.SUCCESS);
                res.setSuccess(isSuccess);
                if (isSuccess) {
                    if (jsonObj.has(TvMenuInfo.SERVICE)) {
                        res.setService(jsonObj.getInt(TvMenuInfo.SERVICE));
                    }
                    JSONArray ipTvItemsArr = jsonObj.getJSONArray(TvMenuItem.JSON_NAME);
                    for (int i = 0; i < ipTvItemsArr.length(); i++) {
                        JSONObject localJSONObject = ipTvItemsArr.getJSONObject(i);
                        TvMenuItem item = new TvMenuItem();
                        item.setId(localJSONObject.getInt(TvMenuItem.ID));
                        item.setName(localJSONObject.getString(TvMenuItem.NAME));
                        res.addItem(item);
                    }
                }
            } else if (jsonObj.has(TvMenuInfo.ERROR)) {
                String error = jsonObj.getString(TvMenuInfo.ERROR);
                res.setError(error);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            res.setSuccess(false);
            return res;
        }
        return res;
    }

    public void macFindTvMenu(Fragment fragment, final ResultListener mListener) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.IpTvMenu.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                TvMenuInfo res = parseJsonTvMenuInfo(result);
                if (mListener != null) {
                    mListener.onResult(res);
                }
            }

            @Override
            public void onLoadError(String errMsg) {
                if (mListener != null) {
                    mListener.onResult(errMsg);
                }
            }
        });
    }

    public void macFindVodMenu(Fragment fragment, final ResultListener mListener) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.VodMenu.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                TvMenuInfo res = parseJsonTvMenuInfo(result);
                if(mListener != null) {
                    mListener.onResult(res);
                }
            }

            @Override
            public void onLoadError(String errMsg) {
                if (mListener != null) {
                    mListener.onResult(errMsg);
                }
            }
        });
    }

    private ChannelsInfo parseChannelsInfo(String result) {
        ChannelsInfo res = new ChannelsInfo();
        try {
            JSONObject jsonObj = new JSONObject(result);
            boolean isSuccess = jsonObj.getBoolean(ChannelsInfo.SUCCESS);
            res.setSuccess(isSuccess);
            if (isSuccess) {
                res.setTotal(jsonObj.getInt(ChannelsInfo.TOTAL));
                JSONArray channelsArr = jsonObj.getJSONArray(Channel.JSON_NAME);
                for (int i = 0; i < channelsArr.length(); i++) {
                    JSONObject channelObj = channelsArr.getJSONObject(i);
                    Channel channel = new Channel();
                    channel.setId(channelObj.getInt(Channel.ID));
                    channel.setName(channelObj.getString(Channel.NAME));
                    channel.setLogo(channelObj.getString(Channel.LOGO));
                    channel.setFavorits(channelObj.getBoolean(Channel.FAVORITS));
                    if (channelObj.has(Channel.ARCHIVE)) {
                        channel.setArchive(channelObj.getString(Channel.ARCHIVE));
                    }
                    //res.Iptv_channels.IptvChanelsItems.allowed.addElement(Boolean.valueOf
                    //        (localJSONObject.getBoolean("allowed")));

                    //res.Iptv_channels.IptvChanelsItems.logo.addElement("/lev.png");
                    res.addChannel(channel);
                }
            }
        } catch (JSONException paramObject) {
            paramObject.printStackTrace();
            res.setSuccess(false);
        }
        return res;
    }

    public List<List<Channel>> macFindChannels(List<TvMenuItem> tvMenuItems) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetChannels.URL);
        final List<List<Channel>> res = new ArrayList<>(tvMenuItems.size());
        for (int i = 0; i < tvMenuItems.size(); i++) {
            IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
            loadInfo.addParam(ApiConstants.GetChannels.PARAM_GENRE_ID, "" + tvMenuItems.get(i)
                    .getId());
            loadInfo.addParam(ApiConstants.GetChannels.PARAM_PER_PAGE, "" + 0);
            loadInfo.addParam(ApiConstants.GetChannels.PARAM_PAGE, "" + 0);
            OkHttpClientRunnable mRunnable = new OkHttpClientRunnable(url, loadInfo);
            mRunnable.setOnLoadResultListener(new OkHttpClientRunnable.OnLoadResultListener() {
                @Override
                public void onLoadResult(boolean isSuccess, String result) {
                    if (isSuccess) {
                        ChannelsInfo info = parseChannelsInfo(result);
                        res.add(new ArrayList<>(info.getUnmodifiableChannels()));
                    }
                }
            });
            mRunnable.run();
        }
        return res;
    }

    public void macFindChannels(Fragment fragment, int categoryId, final ResultListener
            mListener) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetChannels.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetChannels.PARAM_GENRE_ID, "" + categoryId);
        loadInfo.addParam(ApiConstants.GetChannels.PARAM_PER_PAGE, "" + 0);
        loadInfo.addParam(ApiConstants.GetChannels.PARAM_PAGE, "" + 0);
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                ChannelsInfo res = parseChannelsInfo(result);
                if (mListener != null) {
                    mListener.onResult(res);
                }
            }

            @Override
            public void onLoadError(String errMsg) {
                if (mListener != null) {
                    mListener.onResult(errMsg);
                }
            }
        });
    }

    public void macToggleIpTvFavorites(Fragment fragment, int channelId, final ResultListener
            mListener) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.ToggleIpTvFavorites.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.ToggleIpTvFavorites.PARAM_IP_TV, channelId + "");
        executeHttpTask(null, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                boolean res = false;
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    res = jsonObj.getBoolean("success");
                } catch (JSONException e) {
                    parseExceptionAndShowToast(result);
                }

                if (mListener != null) {
                    mListener.onResult(res);
                }
            }

            @Override
            public void onLoadError(String errMsg) {
                if (mListener != null) {
                    mListener.onResult(errMsg);
                }
            }
        });
    }

    public void macFindChannelIp(Fragment fragment, int channelId, final ResultListener mListener)
            throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetChannelIp.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetChannelIp.PARAM_ID, channelId + "");
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                String res = null;
                try {
                    JSONObject localJSONObject = new JSONObject(result);
                    if (localJSONObject.getBoolean("success")) {
                        res = (String) localJSONObject.get("ip");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (mListener != null) {
                    mListener.onResult(res);
                }
            }

            @Override
            public void onLoadError(String errMsg) {
                if (mListener != null) {
                    mListener.onResult(errMsg);
                }
            }
        });
    }

    public void macKeepAlive(Fragment fragment, final ResultListener mListener) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.KeepAlive.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                long res = -1;
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    res = jsonObj.getLong("time");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (mListener != null) {
                    mListener.onResult(res);
                }
            }

            @Override
            public void onLoadError(String errMsg) {
                if (mListener != null) {
                    mListener.onResult(errMsg);
                }
            }
        });
    }

    public void checkAvailability(Fragment fragment, String url, final ResultListener mListener)
            throws IOException {
        String runUrl = ApiUtils.getApiUrl(ApiConstants.LoginPasswordAuth.CheckAvailability.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.LoginPasswordAuth.CheckAvailability.PARAM_URL, url);
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                boolean res = false;
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    res = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (mListener != null) {
                    mListener.onResult(res);
                }
            }

            @Override
            public void onLoadError(String errMsg) {
                if (mListener != null) {
                    mListener.onResult(errMsg);
                }
            }
        });
    }

    private void parseExceptionAndShowToast(String out) {
        try {
            JSONObject obj = new JSONObject(out);
            String error = obj.getString("error");
            Toast.makeText(VideoStreamApp.getInstance().getApplicationContext(), error, Toast
                    .LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    public FilmsInfo getFilms(int genreId) throws IOException {
        FilmsInfo res = new FilmsInfo();
        String url = ApiUtils.getApiUrl(ApiConstants.LoginPasswordAuth.GET_FILMS_URL, "" + genreId,
                "" + 0, "" + 0);
        this.httpOut = this.gets.get(url);
        try {
            JSONObject jsonObj = new JSONObject(this.httpOut);
            boolean isSuccess = jsonObj.getBoolean(FilmsInfo.SUCCESS);
            res.setSuccess(isSuccess);
            if (isSuccess) {
                res.setTotal(jsonObj.getInt(FilmsInfo.TOTAL));

                JSONArray filmsArr = jsonObj.getJSONArray(FilmItem.JSON_NAME);
                for (int i = 0; i < filmsArr.length(); i++) {
                    JSONObject filmJson = filmsArr.getJSONObject(i);
                    FilmItem filmItem = new FilmItem();
                    filmItem.setId(filmJson.getInt(FilmItem.ID));
                    filmItem.setName(filmJson.getString(FilmItem.NAME));
                    filmItem.setLogo(filmJson.getString(FilmItem.LOGO));
                    filmItem.setYear(filmJson.getInt(FilmItem.YEAR));
                    filmItem.setOrigin(filmJson.getString(FilmItem.ORIGIN));
                    filmItem.setGenre(filmJson.getString(FilmItem.GENRE));
                    res.addFilmItem(filmItem);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            res.setSuccess(false);
            return res;
        }
        return res;
    }

    public String GetArchiveUrl(int param1, int param2) throws IOException, JSONException {
        String url = this.mApiPath + "/ws/GetArchiveUrl?id=" + param1 + "&timestamp=" +
                param2;
        this.httpOut = this.gets.get(url);
        JSONObject res = new JSONObject(this.httpOut);
        if (res.has("success")) {
            return res.get("url").toString();
        }
        return "error";
    }

    public LevtvStruct GetEpg(int param1, int param2, int param3, int param4)
            throws IOException, JSONException {
        LevtvStruct res = new LevtvStruct();
        String url = this.mApiPath + "/ws/GetEpg?serviceId=" + param1 +
                "&channelId=" + param2 + "&perPage=1&page=" + param4;
        this.httpOut = this.gets.get(url);

        JSONObject jsonObj = new JSONObject(this.httpOut);
        if (jsonObj.getBoolean("success")) {
            res.Iptv_epg.day = jsonObj.getInt("day");
            res.Iptv_epg.dayOfWeek = jsonObj.getInt("dayOfWeek");
            if (!jsonObj.has("description")) {
                res.Iptv_epg.total = jsonObj.getInt("total");
                url = this.mApiPath + "/ws/GetEpg?serviceId=" + param1 + "&channelId=" +
                        param2 + "&perPage=" + res.Iptv_epg.total + "&page=" + param4;
                this.httpOut = this.gets.get(url);

            }
            return res;
        } else {
            return null;
        }
    }

    public void KeepAlive(boolean keepAlive) {
        //gets.keepAlive(keepAlive);
        new Thread() {
            /* Error * /
            public void run() {

                /*SyncHttpClient client = null;
                try {
                    client = gets.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                if (client != null) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        client.get(mApiPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }      * /
                // Byte code:
                //   0: new 27	ua/ic/levtv/library/SyncHttpClient
                //   3: dup
                //   4: invokespecial 28	ua/ic/levtv/library/SyncHttpClient:<init>	()V
                //   7: astore_1
                //   8: aload_0
                //   9: getfield 15	ua/ic/levtv/library/LevtvDbApi$1:this$0	Lua/ic/levtv/library/LevtvDbApi;
                //   12: getfield 32	ua/ic/levtv/library/LevtvDbApi:gets	Lua/ic/levtv/library/SyncHttpClient;
                //   15: invokevirtual 36	ua/ic/levtv/library/SyncHttpClient:clone	()Lua/ic/levtv/library/SyncHttpClient;
                //   18: astore_2
                //   19: aload_2
                //   20: astore_1
                //   21: ldc2_w 37
                //   24: invokestatic 42	ua/ic/levtv/library/LevtvDbApi$1:sleep	(J)V
                //   27: new 44	java/lang/String
                //   30: dup
                //   31: new 46	java/lang/StringBuilder
                //   34: dup
                //   35: aload_0
                //   36: getfield 15	ua/ic/levtv/library/LevtvDbApi$1:this$0	Lua/ic/levtv/library/LevtvDbApi;
                //   39: getfield 50	ua/ic/levtv/library/LevtvDbApi:mApiPath	Ljava/lang/String;
                //   42: invokestatic 54	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
                //   45: invokespecial 57	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
                //   48: ldc 59
                //   50: invokevirtual 63	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   53: invokevirtual 67	java/lang/StringBuilder:toString	()Ljava/lang/String;
                //   56: invokespecial 68	java/lang/String:<init>	(Ljava/lang/String;)V
                //   59: astore_2
                //   60: aload_1
                //   61: aload_2
                //   62: invokevirtual 72	ua/ic/levtv/library/SyncHttpClient:get	(Ljava/lang/String;)Ljava/lang/String;
                //   65: pop
                //   66: goto -45 -> 21
                //   69: astore_2
                //   70: aload_2
                //   71: invokevirtual 75	java/io/IOException:printStackTrace	()V
                //   74: goto -53 -> 21
                //   77: astore_1
                //   78: aload_1
                //   79: invokevirtual 76	java/lang/InterruptedException:printStackTrace	()V
                //   82: return
                //   83: astore_2
                //   84: aload_2
                //   85: invokevirtual 77	java/lang/CloneNotSupportedException:printStackTrace	()V
                //   88: goto -67 -> 21
                // Local variable table:
                //   start	length	slot	name	signature
                //   0	91	0	this	1
                //   7	54	1	localObject1	Object
                //   77	2	1	localInterruptedException	InterruptedException
                //   18	44	2	localObject2	Object
                //   69	2	2	localIOException	IOException
                //   83	2	2	localCloneNotSupportedException	CloneNotSupportedException
                // Exception table:
                //   from	to	target	type
                //   60	66	69	java/io/IOException
                //   21	60	77	java/lang/InterruptedException
                //   60	66	77	java/lang/InterruptedException
                //   70	74	77	java/lang/InterruptedException
                //   8	19	83	java/lang/CloneNotSupportedException
            }
        }.start();
    }

    public String OrderFilm(int param) throws IOException, JSONException {
        return "http://193.53.89.7/vod/Amelia.mp4";
    }

    public void getAuthVod() {
        String str = new String(this.applicationPathVod + "/ws/AuthStb?sn=CS1630K8080A3211116000357&mac=00:1a:d0:07:a1:62");
        try {
            this.httpOut = this.gets.get(str);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LevtvStruct getFilm(int paramInt) throws JSONException {
        LevtvStruct res = new LevtvStruct();
        String url = this.applicationPathVod + "/ws/GetFilm?id=" + paramInt;
        try {
            this.httpOut = this.gets.get(url);
            JSONObject jsonObj = new JSONObject(this.httpOut);
            if (jsonObj.getBoolean("success")) {
                //TODO uncomment
                //res.Films_struct.success = true;
                res.Film_struct.id = jsonObj.getInt("id");
                res.Film_struct.year = jsonObj.getInt("year");
                res.Film_struct.price = jsonObj.getInt("price");
                res.Film_struct.duration = jsonObj.getInt("duration");
                res.Film_struct.actor = jsonObj.getString("actor");
                res.Film_struct.country = jsonObj.getString("country");
                res.Film_struct.genre = jsonObj.getString("genre");
                res.Film_struct.logo = jsonObj.getString("logo");
                res.Film_struct.name = jsonObj.getString("name");
                res.Film_struct.origin = jsonObj.getString("origin");
                res.Film_struct.description = jsonObj.getString("description");
            }
        } catch (IOException e) {
            e.printStackTrace();
            //TODO uncomment
            //res.Films_struct.success = false;
            System.out.println("is Active false");
        }
        return res;
    }

    public void getKeepAliveVod() {
        new Thread() {
            /* Error * /
            public void run() {
                // Byte code:
                //   0: new 27	ua/ic/levtv/library/SyncHttpClient
                //   3: dup
                //   4: invokespecial 28	ua/ic/levtv/library/SyncHttpClient:<init>	()V
                //   7: astore_1
                //   8: aload_0
                //   9: getfield 15	ua/ic/levtv/library/LevtvDbApi$2:this$0	Lua/ic/levtv/library/LevtvDbApi;
                //   12: getfield 32	ua/ic/levtv/library/LevtvDbApi:gets	Lua/ic/levtv/library/SyncHttpClient;
                //   15: invokevirtual 36	ua/ic/levtv/library/SyncHttpClient:clone	()Lua/ic/levtv/library/SyncHttpClient;
                //   18: astore_2
                //   19: aload_2
                //   20: astore_1
                //   21: ldc2_w 37
                //   24: invokestatic 42	ua/ic/levtv/library/LevtvDbApi$2:sleep	(J)V
                //   27: new 44	java/lang/String
                //   30: dup
                //   31: new 46	java/lang/StringBuilder
                //   34: dup
                //   35: aload_0
                //   36: getfield 15	ua/ic/levtv/library/LevtvDbApi$2:this$0	Lua/ic/levtv/library/LevtvDbApi;
                //   39: getfield 50	ua/ic/levtv/library/LevtvDbApi:applicationPathVod	Ljava/lang/String;
                //   42: invokestatic 54	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
                //   45: invokespecial 57	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
                //   48: ldc 59
                //   50: invokevirtual 63	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   53: invokevirtual 67	java/lang/StringBuilder:toString	()Ljava/lang/String;
                //   56: invokespecial 68	java/lang/String:<init>	(Ljava/lang/String;)V
                //   59: astore_2
                //   60: aload_1
                //   61: aload_2
                //   62: invokevirtual 72	ua/ic/levtv/library/SyncHttpClient:get	(Ljava/lang/String;)Ljava/lang/String;
                //   65: pop
                //   66: goto -45 -> 21
                //   69: astore_2
                //   70: aload_2
                //   71: invokevirtual 75	java/io/IOException:printStackTrace	()V
                //   74: goto -53 -> 21
                //   77: astore_1
                //   78: aload_1
                //   79: invokevirtual 76	java/lang/InterruptedException:printStackTrace	()V
                //   82: return
                //   83: astore_2
                //   84: aload_2
                //   85: invokevirtual 77	java/lang/CloneNotSupportedException:printStackTrace	()V
                //   88: goto -67 -> 21
                // Local variable table:
                //   start	length	slot	name	signature
                //   0	91	0	this	2
                //   7	54	1	localObject1	Object
                //   77	2	1	localInterruptedException	InterruptedException
                //   18	44	2	localObject2	Object
                //   69	2	2	localIOException	IOException
                //   83	2	2	localCloneNotSupportedException	CloneNotSupportedException
                // Exception table:
                //   from	to	target	type
                //   60	66	69	java/io/IOException
                //   21	60	77	java/lang/InterruptedException
                //   60	66	77	java/lang/InterruptedException
                //   70	74	77	java/lang/InterruptedException
                //   8	19	83	java/lang/CloneNotSupportedException
            }
        }.start();
    }

    public LevtvStruct getOsd(int param1, int param2) throws IOException, JSONException {
        LevtvStruct res = new LevtvStruct();
        String url = this.mApiPath + "/ws/GetChannelOsd?channelId=" + param1 +
                "&serviceId=" + param2;
        this.httpOut = this.gets.get(url);
        JSONObject jsonObj = new JSONObject(this.httpOut);
        res.Osd_struct.success = false;
        if (jsonObj.has("description")) {
            res.Osd_struct.success = false;
        }
        if (jsonObj.has("programs")) {
            res.Osd_struct.success = true;
            JSONArray jsonArr = jsonObj.getJSONArray("programs");
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject paramObject2 = jsonArr.getJSONObject(i);
                res.Osd_struct.IptvOsdItems.currTime.addElement(Integer.valueOf(paramObject2.getInt("currTime")));
                res.Osd_struct.IptvOsdItems.duration.addElement(Integer.valueOf(paramObject2.getInt("duration")));
                res.Osd_struct.IptvOsdItems.durationTime.addElement(Integer.valueOf(paramObject2.getInt("durationTime")));
                res.Osd_struct.IptvOsdItems.end.addElement(paramObject2.getString("end"));
                res.Osd_struct.IptvOsdItems.start.addElement(paramObject2.getString("start"));
                res.Osd_struct.IptvOsdItems.title.addElement(paramObject2.getString("title"));
            }
        }
        return res;
    }

    public LevtvStruct getOsdArch(String param1, String param2, Integer paramInteger)
            throws IOException, JSONException {
        LevtvStruct res = new LevtvStruct();
        String url = this.mApiPath + "/ws/GetChannelOsd?channelId=" +
                param1 + "&serviceId=" + param2 + "&timestamp=" + paramInteger;
        this.httpOut = this.gets.get(url);
        JSONObject jsonObj = new JSONObject(this.httpOut);
        res.Osd_struct.success = false;
        if (jsonObj.has("description")) {
            res.Osd_struct.success = false;
        } else {
            res.Osd_struct.success = true;
            if (jsonObj.has("programs")) {
                res.Osd_struct.success = true;
                JSONArray jsonArr = jsonObj.getJSONArray("programs");
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObject = jsonArr.getJSONObject(i);
                    res.Osd_struct.IptvOsdItems.currTime.addElement(Integer.valueOf(jsonObject.getInt
                            ("currTime")));
                    res.Osd_struct.IptvOsdItems.duration.addElement(Integer.valueOf(jsonObject.getInt("duration")));
                    res.Osd_struct.IptvOsdItems.durationTime.addElement(Integer.valueOf(jsonObject.getInt("durationTime")));
                    res.Osd_struct.IptvOsdItems.end.addElement(jsonObject.getString("end"));
                    res.Osd_struct.IptvOsdItems.start.addElement(jsonObject.getString("start"));
                    res.Osd_struct.IptvOsdItems.title.addElement(jsonObject.getString("title"));
                }
            }
        }
        return res;
    }

    public String getApiPath() {
        return mApiPath;
    }

    public String getApplicationPathVod() {
        return applicationPathVod;
    }       */
}
