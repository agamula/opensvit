package ua.utils;

import java.util.Locale;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApplication;

public class Utils {

    private static String sApiUrl;

    private Utils() {

    }

    public static String getApiUrl() {
        VideoStreamApplication app = VideoStreamApplication.getInstance();
        sApiUrl = app.getResources().getString(app.isTest() ? R.string.api_test_url : R.string
                .api_live_url);
        return sApiUrl;
    }

    public static String getApiUrl(String addUrl, String... formatParams) {
        return String.format(Locale.US, sApiUrl + addUrl, formatParams);
    }
}
