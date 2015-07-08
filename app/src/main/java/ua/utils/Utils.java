package ua.utils;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApplication;

public class Utils {
    private Utils() {

    }

    public static String getApiUrl() {
        VideoStreamApplication app = VideoStreamApplication.getInstance();
        return app.getResources().getString(app.isTest() ? R.string.api_test_url : R.string
                .api_live_url);
    }
}
