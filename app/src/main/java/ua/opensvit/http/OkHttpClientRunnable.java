package ua.opensvit.http;

import android.content.res.Resources;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.Map;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;

public class OkHttpClientRunnable implements Runnable {
    private final String url;
    private final IOkHttpLoadInfo info;
    private OnLoadResultListener mOnLoadResultListener;
    private boolean mErrorLogger;
    private final Map<String, String> mHeaders = new HashMap<>();
    private static final OkHttpClient CLIENT = new OkHttpClient();

    static {
        java.net.CookieManager cookieManager = new java.net.CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CLIENT.setCookieHandler(cookieManager);
    }

    private final Resources mResources;

    public OkHttpClientRunnable(String url, IOkHttpLoadInfo mOkHttpClientInfo) {
        this.url = url;
        this.info = mOkHttpClientInfo;
        this.mResources = VideoStreamApp.getInstance().getResources();
    }

    public void setOnLoadResultListener(OnLoadResultListener moOnLoadResultListener) {
        this.mOnLoadResultListener = moOnLoadResultListener;
    }

    public void setErrorLogger(boolean mErrorLogger) {
        this.mErrorLogger = mErrorLogger;
    }

    public void addHeader(String headerName, String headerValue) {
        mHeaders.put(headerName, headerValue);
    }

    public interface OnLoadResultListener {
        void onLoadResult(boolean isSuccess, String result);
    }

    @Override
    public void run() {
        try {
            Request.Builder builder = new Request.Builder();
            switch (info.requestType()) {
                case GET:
                    builder.get();
                    break;
                case HEAD:
                    builder.head();
                    break;
                case POST:
                    builder.post(info.requestBody());
                    break;
                case PUT:
                    builder.put(info.requestBody());
                    break;
                case PATCH:
                    builder.patch(info.requestBody());
                    break;
                case DELETE:
                    builder.delete();
                    break;
                default:
                    if(mOnLoadResultListener != null) {
                        mOnLoadResultListener.onLoadResult(false, mResources.getString(R.string
                                .wrong_http_method_message));
                    }
            }

            for (String headerName : mHeaders.keySet()) {
                builder = builder.addHeader(headerName, mHeaders.get(headerName));
            }

            String url = info.newUrl(this.url);

            Request request = builder
                    .url(url)
                    .build();

            Response response = CLIENT.newCall(request).execute();
            if(mOnLoadResultListener != null) {
                if (!response.isSuccessful()) {
                    if (mErrorLogger) {
                        mOnLoadResultListener.onLoadResult(false, response.code() + " : " +
                                response.message());
                    } else {
                        mOnLoadResultListener.onLoadResult(false, mResources.getString(R.string
                                .load_failed_message));
                    }
                } else {
                    mOnLoadResultListener.onLoadResult(true, response.body().string());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(mOnLoadResultListener != null) {
                mOnLoadResultListener.onLoadResult(false, mResources.getString(R.string
                        .load_failed_message));
            }
        }
    }
}
