package ua.opensvit.api;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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
import ua.opensvit.data.CreepingLineItem;
import ua.opensvit.data.GetUrlItem;
import ua.opensvit.data.InfoAbout;
import ua.opensvit.data.constants.ApiConstants;
import ua.opensvit.data.authorization.login_password.AuthorizationInfo;
import ua.opensvit.data.authorization.login_password.UserProfile;
import ua.opensvit.data.authorization.mac.AuthorizationInfoMac;
import ua.opensvit.data.authorization.mac.UserProfileMac;
import ua.opensvit.data.channels.Channel;
import ua.opensvit.data.channels.ChannelsInfo;
import ua.opensvit.data.epg.EpgItem;
import ua.opensvit.data.images.ImageInfo;
import ua.opensvit.data.images.ImageItem;
import ua.opensvit.data.menu.TvMenuInfo;
import ua.opensvit.data.menu.TvMenuItem;
import ua.opensvit.data.osd.OsdItem;
import ua.opensvit.data.osd.ProgramDurationItem;
import ua.opensvit.http.IOkHttpLoadInfo;
import ua.opensvit.http.OkHttpAsyncTask;
import ua.opensvit.http.OkHttpClientRunnable;
import ua.opensvit.utils.ApiUtils;
import ua.opensvit.utils.HttpRequestsCreator;
import ua.opensvit.utils.ParseUtils;

public class OpenWorldApi1 {

    public interface ResultListener {
        void onResult(Object res);

        void onError(String result);
    }

    private void executeAuthorizationInfoMac(ProgressBar mProgress, String
            url, final ResultListener mListener, String... params) throws IOException {
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        for (int i = 0; i < params.length; i += 2) {
            loadInfo.addParam(params[i], params[i + 1]);
        }
        executeHttpTask(mProgress, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                AuthorizationInfoMac res = new AuthorizationInfoMac();
                res.setUserProfileBase(new UserProfileMac());
                if (!ParseUtils.parseAuthMac(res, result)) {
                    res.setUserProfileBase(null);
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

    public void macAuth(ProgressBar mProgress, ResultListener mListener)
            throws IOException {
        WifiManager manager = (WifiManager) VideoStreamApp.getInstance()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        if (info != null) {
            String mac = info.getMacAddress();
            String sn = Build.SERIAL;
            String url = ApiUtils.getApiUrl(ApiConstants.MacAddressAuth.Auth.URL);

            executeAuthorizationInfoMac(mProgress, url, mListener,
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

            executeAuthorizationInfoMac((ProgressBar) fragment.getActivity().findViewById(R.id
                    .progress), url, mListener, ApiConstants
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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                AuthorizationInfo res = new AuthorizationInfo();
                res.setUserProfileBase(new UserProfile());

                if (!ParseUtils.parseAuthLoginPassword(res, result)) {
                    res.setUserProfileBase(null);
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

    private AsyncTask executeHttpTaskFragment(Fragment fragment, String url, IOkHttpLoadInfo
            .GetLoaderCreateInfo
            loadInfo, OkHttpAsyncTask.OnLoadFinishedListener mLoadFinishedListener) {
        final ProgressBar progressBar;
        if (fragment == null) {
            progressBar = null;
        } else {
            progressBar = (ProgressBar) fragment.getActivity().findViewById(R.id.progress);
        }
        return executeHttpTask(progressBar, url, loadInfo, mLoadFinishedListener);
    }

    private AsyncTask executeHttpTask(ProgressBar progressBar, String url, IOkHttpLoadInfo
            .GetLoaderCreateInfo
            loadInfo, OkHttpAsyncTask.OnLoadFinishedListener mLoadFinishedListener) {
        return HttpRequestsCreator.createTask(progressBar, url, loadInfo, mLoadFinishedListener)
                .execute();
    }

    public void macIpTvMenu(Fragment fragment, final ResultListener mListener) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.IpTvMenu.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                TvMenuInfo res = ParseUtils.parseTvMenuInfo(result);
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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                TvMenuInfo res = ParseUtils.parseTvMenuInfo(result);
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

    public List<List<Channel>> macGetChannels(List<TvMenuItem> tvMenuItems) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetChannels.URL);
        final List<List<Channel>> res = new ArrayList<>(tvMenuItems.size());
        for (int i = 0; i < tvMenuItems.size(); i++) {
            IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
            loadInfo.addParam(ApiConstants.GetChannels.PARAM_GENRE_ID, "" + tvMenuItems.get(i)
                    .getId());
            loadInfo.addParam(ApiConstants.GetChannels.PARAM_PER_PAGE, "" + 0);
            loadInfo.addParam(ApiConstants.GetChannels.PARAM_PAGE, "" + 0);
            OkHttpClientRunnable mRunnable = HttpRequestsCreator.createRunnable(url, loadInfo);
            mRunnable.setOnLoadResultListener(new OkHttpClientRunnable.OnLoadResultListener() {
                @Override
                public void onLoadResult(boolean isSuccess, String result) {
                    if (isSuccess) {
                        ChannelsInfo info = ParseUtils.parseChannelsInfo(result);
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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                ChannelsInfo res = ParseUtils.parseChannelsInfo(result);
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
        executeHttpTaskFragment(null, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
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

    public OkHttpClientRunnable macGetArchiveUrlRunnable(int channelId, long timestamp, final
    ResultListener
            mListener) {
        String url = ApiUtils.getApiUrl(ApiConstants.GetArchiveUrl.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetArchiveUrl.PARAM_ID, channelId + "");
        loadInfo.addParam(ApiConstants.GetArchiveUrl.PARAM_TIMESTAMP, timestamp + "");
        OkHttpClientRunnable runnable = HttpRequestsCreator.createRunnable(url, loadInfo);
        runnable.setOnLoadResultListener(new OkHttpClientRunnable.OnLoadResultListener() {
            @Override
            public void onLoadResult(boolean isSuccess, String result) {
                if (isSuccess) {
                    GetUrlItem res = ParseUtils.parseGetUrl(result);
                    mListener.onResult(res);
                } else {
                    mListener.onError(result);
                }
            }
        });
        return runnable;
    }

    public void macGetArchiveUrl(Fragment fragment, int channelId, long timestamp, final ResultListener
            mListener)
            throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetArchiveUrl.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetArchiveUrl.PARAM_ID, channelId + "");
        loadInfo.addParam(ApiConstants.GetArchiveUrl.PARAM_TIMESTAMP, timestamp + "");
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                GetUrlItem res = ParseUtils.parseGetUrl(result);

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

    public OkHttpClientRunnable macGetChannelIpRunnable(int channelId) throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetChannelIp.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetChannelIp.PARAM_ID, channelId + "");
        return HttpRequestsCreator.createRunnable(url, loadInfo);
    }

    public AsyncTask macGetChannelIp(Fragment fragment, int channelId, final ResultListener mListener)
            throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetChannelIp.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetChannelIp.PARAM_ID, channelId + "");
        return executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask
                .OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                GetUrlItem res = ParseUtils.parseGetUrl(result);

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

    public OkHttpClientRunnable macGetChannelOsd(int channelId, int serviceId, long timestamp) {
        String url = ApiUtils.getApiUrl(ApiConstants.GetChannelOsd.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetChannelOsd.PARAM_CHANNEL_ID, channelId + "");
        loadInfo.addParam(ApiConstants.GetChannelOsd.PARAM_SERVICE_ID, serviceId + "");
        loadInfo.addParam(ApiConstants.GetChannelOsd.PARAM_TIMESTAMP, timestamp + "");
        return HttpRequestsCreator.createRunnable(url, loadInfo);
    }

    public void macGetChannelOsd(Fragment fragment, int channelId, int serviceId, long timestamp,
                                 final ResultListener mListener)
            throws IOException {
        String url = ApiUtils.getApiUrl(ApiConstants.GetChannelOsd.URL);
        IOkHttpLoadInfo.GetLoaderCreateInfo loadInfo = new IOkHttpLoadInfo.GetLoaderCreateInfo();
        loadInfo.addParam(ApiConstants.GetChannelOsd.PARAM_CHANNEL_ID, channelId + "");
        loadInfo.addParam(ApiConstants.GetChannelOsd.PARAM_SERVICE_ID, serviceId + "");
        loadInfo.addParam(ApiConstants.GetChannelOsd.PARAM_TIMESTAMP, timestamp + "");
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                OsdItem res = ParseUtils.parseOsd(result);

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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                CreepingLineItem res = ParseUtils.parseCreepingLine(result);

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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
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
        OkHttpClientRunnable runnable = HttpRequestsCreator.createRunnable(url, loadInfo);
        runnable.setOnLoadResultListener(new OkHttpClientRunnable.OnLoadResultListener() {
            @Override
            public void onLoadResult(boolean isSuccess, String result) {
                if (isSuccess) {
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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                ImageInfo res = ParseUtils.parseImageInfo(result);

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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
            @Override
            public void onLoadFinished(String result) {
                InfoAbout res = ParseUtils.parseInfoAbout(result);

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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
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
        executeHttpTaskFragment(fragment, url, loadInfo, new OkHttpAsyncTask.OnLoadFinishedListener() {
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
