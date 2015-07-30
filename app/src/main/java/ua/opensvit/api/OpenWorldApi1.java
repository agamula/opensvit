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
import java.util.Objects;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.data.CreepingLineItem;
import ua.opensvit.data.GetUrlItem;
import ua.opensvit.data.InfoAbout;
import ua.opensvit.data.constants.ApiConstants;
import ua.opensvit.data.authorization.AuthorizationInfoBase;
import ua.opensvit.data.authorization.UserProfileBase;
import ua.opensvit.data.authorization.login_password.AuthorizationInfo;
import ua.opensvit.data.authorization.login_password.UserInfo;
import ua.opensvit.data.authorization.login_password.UserProfile;
import ua.opensvit.data.authorization.mac.AuthorizationInfoMac;
import ua.opensvit.data.authorization.mac.UserProfileMac;
import ua.opensvit.data.channels.Channel;
import ua.opensvit.data.channels.ChannelsInfo;
import ua.opensvit.data.epg.EpgItem;
import ua.opensvit.data.epg.ProgramItem;
import ua.opensvit.data.images.ImageInfo;
import ua.opensvit.data.images.ImageItem;
import ua.opensvit.data.menu.TvMenuInfo;
import ua.opensvit.data.menu.TvMenuItem;
import ua.opensvit.data.osd.OsdItem;
import ua.opensvit.http.IOkHttpLoadInfo;
import ua.opensvit.http.OkHttpAsyncTask;
import ua.opensvit.http.OkHttpClientRunnable;
import ua.opensvit.utils.ApiUtils;
import ua.opensvit.utils.ParseUtils;

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

    public void macIpTvMenu(Fragment fragment, final ResultListener mListener) throws IOException {
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
                    mListener.onError(errMsg);
                }
            }
        });
    }

    public void macVodMenu(Fragment fragment, final ResultListener mListener) throws IOException {
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
                    mListener.onError(errMsg);
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

    public List<List<Channel>> macGetChannels(List<TvMenuItem> tvMenuItems) throws IOException {
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

    public void macGetChannels(Fragment fragment, int categoryId, final ResultListener
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
                    mListener.onError(errMsg);
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
                    mListener.onError(errMsg);
                }
            }
        });
    }

    public void macGetArchiveUrl(Fragment fragment, int id, long timestamp, final ResultListener
            mListener)
            throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetArchiveUrl.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetArchiveUrl.PARAM_ID, id + "");
        loadInfo.addParam(ApiConstants.GetArchiveUrl.PARAM_TIMESTAMP, timestamp + "");
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                GetUrlItem res = new GetUrlItem();
                try {
                    JSONObject localJSONObject = new JSONObject(result);
                    if(localJSONObject.has(GetUrlItem.IP)) {
                        res.setUrl(localJSONObject.getString(GetUrlItem.IP));
                    } else {
                        res.setUrl(localJSONObject.getString(GetUrlItem.URL));
                    }
                    res.setHasInfoLine(localJSONObject.getBoolean(GetUrlItem.HAS_INFO_LINE));
                    res.setSuccess(localJSONObject.getBoolean(GetUrlItem.SUCCESS));
                } catch (JSONException e) {
                    e.printStackTrace();
                    res = null;
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

    public void macGetChannelIp(Fragment fragment, int channelId, final ResultListener mListener)
            throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetChannelIp.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetChannelIp.PARAM_ID, channelId + "");
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                GetUrlItem res = new GetUrlItem();
                try {
                    JSONObject localJSONObject = new JSONObject(result);
                    if(localJSONObject.has(GetUrlItem.IP)) {
                        res.setUrl(localJSONObject.getString(GetUrlItem.IP));
                    } else {
                        res.setUrl(localJSONObject.getString(GetUrlItem.URL));
                    }
                    res.setHasInfoLine(localJSONObject.getBoolean(GetUrlItem.HAS_INFO_LINE));
                    res.setSuccess(localJSONObject.getBoolean(GetUrlItem.SUCCESS));
                } catch (JSONException e) {
                    e.printStackTrace();
                    res = null;
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

    public void macGetChannelOsd(Fragment fragment, int channelId, int serviceId, long timestamp,
                                 final ResultListener mListener)
            throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetChannelOsd.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetChannelOsd.PARAM_CHANNEL_ID, channelId + "");
        loadInfo.addParam(ApiConstants.GetChannelOsd.PARAM_SERVICE_ID, serviceId + "");
        loadInfo.addParam(ApiConstants.GetChannelOsd.PARAM_TIMESTAMP, timestamp + "");
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                OsdItem res = new OsdItem();
                try {
                    JSONObject localJSONObject = new JSONObject(result);
                    JSONArray programsArr = localJSONObject.getJSONArray(ua.opensvit.data.osd
                            .ProgramItem.JSON_NAME);
                    for (int i = 0; i < programsArr.length(); i++) {
                        JSONObject programObj = programsArr.getJSONObject(i);
                        ua.opensvit.data.osd.ProgramItem programItem = new ua.opensvit.data.osd
                                .ProgramItem();
                        programItem.setDuration(programObj.getInt(ua.opensvit.data.osd
                                .ProgramItem.DURATION));
                        programItem.setTitle(programObj.getString(ua.opensvit.data.osd
                                .ProgramItem.TITLE));
                        programItem.setStart(programObj.getString(ua.opensvit.data.osd
                                .ProgramItem.START));
                        programItem.setEnd(programObj.getString(ua.opensvit.data.osd
                                .ProgramItem.END));
                        res.addProgram(programItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    res = null;
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

    public void macUnusedGetCreepingLine(Fragment fragment, int service, int looking, final
    ResultListener mListener) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetCreepingLine.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetCreepingLine.PARAM_SERVICE, service + "");
        loadInfo.addParam(ApiConstants.GetCreepingLine.PARAM_LOOKING, looking + "");
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                CreepingLineItem res = new CreepingLineItem();
                try {
                    JSONObject localJSONObject = new JSONObject(result);
                    res.setSuccess(localJSONObject.getBoolean(CreepingLineItem.SUCCESS));
                    res.setText(localJSONObject.getString(CreepingLineItem.TEXT));
                } catch (JSONException e) {
                    e.printStackTrace();
                    res = null;
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

    public void macGetEpg(Fragment fragment, int channelId, int serviceId, long startUT, long
            endUT, int perPage, int page, final ResultListener mListener) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetEpg.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetEpg.PARAM_CHANNEL_ID, channelId + "");
        loadInfo.addParam(ApiConstants.GetEpg.PARAM_SERVICE_ID, serviceId + "");
        loadInfo.addParam(ApiConstants.GetEpg.PARAM_START_UT, startUT + "");
        loadInfo.addParam(ApiConstants.GetEpg.PARAM_END_UT, endUT + "");
        loadInfo.addParam(ApiConstants.GetEpg.PARAM_PER_PAGE, perPage + "");
        loadInfo.addParam(ApiConstants.GetEpg.PARAM_PAGE, page + "");
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                EpgItem res = ParseUtils.parseEpg(result);

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

    public Runnable macGetEpgRunnable(int channelId, int serviceId, long startUT, long
            endUT, int perPage, int page, final ResultListener mListener) {
        String url = ApiUtils.getApiUrl(ApiConstants.GetEpg.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetEpg.PARAM_CHANNEL_ID, channelId + "");
        loadInfo.addParam(ApiConstants.GetEpg.PARAM_SERVICE_ID, serviceId + "");
        loadInfo.addParam(ApiConstants.GetEpg.PARAM_START_UT, startUT + "");
        loadInfo.addParam(ApiConstants.GetEpg.PARAM_END_UT, endUT + "");
        loadInfo.addParam(ApiConstants.GetEpg.PARAM_PER_PAGE, perPage + "");
        loadInfo.addParam(ApiConstants.GetEpg.PARAM_PAGE, page + "");
        OkHttpClientRunnable runnable = new OkHttpClientRunnable(url, loadInfo);
        runnable.setOnLoadResultListener(new OkHttpClientRunnable.OnLoadResultListener() {
            @Override
            public void onLoadResult(boolean isSuccess, String result) {
                if(isSuccess) {
                    EpgItem res = ParseUtils.parseEpg(result);
                    mListener.onResult(res);
                } else {
                    mListener.onError(result);
                }
            }
        });
        return runnable;
    }

    public void macUnusedGetFilms(Fragment fragment, int genre, int perPage, int page, final
    ResultListener mListener) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetFilms.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetFilms.PARAM_GENRE_ID, genre + "");
        loadInfo.addParam(ApiConstants.GetFilms.PARAM_PER_PAGE, perPage + "");
        loadInfo.addParam(ApiConstants.GetFilms.PARAM_PAGE, page + "");
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                Object res = new Object();
                try {
                    JSONObject localJSONObject = new JSONObject(result);
                    localJSONObject.getBoolean("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                    res = null;
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

    public void macUnusedGetFilm(Fragment fragment, int filmId, final ResultListener mListener) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetFilm.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetFilm.PARAM_ID, filmId + "");
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                Object res = new Object();
                try {
                    JSONObject localJSONObject = new JSONObject(result);
                    localJSONObject.getBoolean("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                    res = null;
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

    public void macGetImages(Fragment fragment, final ResultListener mListener) throws
            IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetImages.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                ImageInfo res = new ImageInfo();
                try {
                    JSONObject localJSONObject = new JSONObject(result);
                    res.setSuccess(localJSONObject.getBoolean(ImageInfo.SUCCESS));
                    JSONArray imageItemsObj = localJSONObject.getJSONArray(ImageItem.JSON_NAME);
                    for (int i = 0; i < imageItemsObj.length(); i++) {
                        ImageItem imageItem = new ImageItem();
                        imageItem.setUrl(imageItemsObj.getString(i));
                        res.addImageItem(imageItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    res = null;
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

    public void macUnusedGetTranslateI18n(Fragment fragment, final ResultListener mListener) throws
            IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.I18n.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                Object res = new Object();
                try {
                    JSONObject localJSONObject = new JSONObject(result);
                    //TODO parse result and save
                } catch (JSONException e) {
                    e.printStackTrace();
                    res = null;
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

    public void macUnusedInfoAbout(Fragment fragment, final ResultListener mListener)
            throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.InfoAbout.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                InfoAbout res = new InfoAbout();
                try {
                    JSONObject localJSONObject = new JSONObject(result);
                    res.setJava(localJSONObject.getString(InfoAbout.JAVA));
                } catch (JSONException e) {
                    e.printStackTrace();
                    res = null;
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
                    mListener.onError(errMsg);
                }
            }
        });
    }

    //TODO make work
    public void macUnusedOrderFilm(Fragment fragment, int id, int pin, final ResultListener mListener)
            throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.OrderFilm.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.OrderFilm.PARAM_ID, id + "");
        loadInfo.addParam(ApiConstants.OrderFilm.PARAM_PIN, pin + "");
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                Object res = new Object();
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    //TODO parse results
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

    //TODO not mac
    public void resetPin(Fragment fragment, int oldPin, int pin, final ResultListener mListener)
            throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.LoginPasswordAuth.ResetPin.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.LoginPasswordAuth.ResetPin.PARAM_OLD_PIN, oldPin + "");
        loadInfo.addParam(ApiConstants.LoginPasswordAuth.ResetPin.PARAM_PIN, pin + "");
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                Object res = new Object();
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    //TODO parse results
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

    public void macUpdateProfile(Fragment fragment, int id, int type, String language, String
            ratio, String resolution, String skin, String transparency, String startPage, String
            networkPath, String volume, int reminder, final ResultListener mListener) throws
            IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.UpdateProfile.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.UpdateProfile.PARAM_ID, id + "");
        loadInfo.addParam(ApiConstants.UpdateProfile.PARAM_TYPE, type + "");
        loadInfo.addParam(ApiConstants.UpdateProfile.PARAM_LANGUAGE, language);
        loadInfo.addParam(ApiConstants.UpdateProfile.PARAM_RATIO, ratio);
        loadInfo.addParam(ApiConstants.UpdateProfile.PARAM_RESOLUTION, resolution);
        loadInfo.addParam(ApiConstants.UpdateProfile.PARAM_SKIN, skin);
        loadInfo.addParam(ApiConstants.UpdateProfile.PARAM_TRANSPARENCY, transparency);
        loadInfo.addParam(ApiConstants.UpdateProfile.PARAM_START_PAGE, startPage);
        loadInfo.addParam(ApiConstants.UpdateProfile.PARAM_NETWORK_PATH, networkPath);
        loadInfo.addParam(ApiConstants.UpdateProfile.PARAM_VOLUME, volume);
        loadInfo.addParam(ApiConstants.UpdateProfile.PARAM_REMINDER, reminder + "");
        executeHttpTask(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                Object res = new Object();
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    //TODO parse results
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
                    mListener.onError(errMsg);
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
}
