package ua.opensvit.utils;

import java.util.Locale;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;

public class ApiUtils {

    private static String sApiUrl;

    private ApiUtils() {

    }

    public static String getBaseUrl() {
        VideoStreamApp app = VideoStreamApp.getInstance();
        if(app.isMac()) {
            if(app.isTest()) {
                sApiUrl = app.getString(R.string.api_test_mac_url);
            } else {
                sApiUrl = app.getString(R.string.api_live_mac_url);
            }
        } else {
            if(app.isTest()) {
                sApiUrl = app.getString(R.string.api_test_login_password_url);
            } else {
                sApiUrl = app.getString(R.string.api_live_login_password_url);
            }
        }
        return sApiUrl;
    }

    public static String getApiUrl(String addUrl, String... formatParams) {
        return String.format(Locale.US, sApiUrl + addUrl, formatParams);
    }
}
