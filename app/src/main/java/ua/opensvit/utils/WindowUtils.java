package ua.opensvit.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Pair;
import android.view.Display;
import android.view.WindowManager;

import ua.opensvit.VideoStreamApp;

public class WindowUtils {
    private WindowUtils() {

    }

    public static int getScreenWidth() {
        Context context = VideoStreamApp.getInstance().getApplicationContext();
        int screenWidth;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= 11) {
            Point p = new Point();
            windowManager.getDefaultDisplay().getSize(p);
            screenWidth = p.x;
        } else {
            Display display = windowManager.getDefaultDisplay();
            screenWidth = display.getWidth();
        }
        return screenWidth;
    }

    public static int getScreenHeight() {
        Context context = VideoStreamApp.getInstance().getApplicationContext();
        int screenHeight;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= 11) {
            Point p = new Point();
            windowManager.getDefaultDisplay().getSize(p);
            screenHeight = p.y;
        } else {
            Display display = windowManager.getDefaultDisplay();
            screenHeight = display.getHeight();
        }
        return screenHeight;
    }

    public static Pair<Integer, Integer> getScreenSizes() {
        Context context = VideoStreamApp.getInstance().getApplicationContext();
        int screenWidth, screenHeight;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= 11) {
            Point p = new Point();
            windowManager.getDefaultDisplay().getSize(p);
            screenWidth = p.x;
            screenHeight = p.y;
        } else {
            Display display = windowManager.getDefaultDisplay();
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
        }
        return new Pair<>(screenWidth, screenHeight);
    }
}
