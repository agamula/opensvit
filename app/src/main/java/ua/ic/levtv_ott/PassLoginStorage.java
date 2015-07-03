package ua.ic.levtv_ott;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PassLoginStorage {
    public static final String STORAGE_NAME = new String("ic_storage");
    private static Context context = null;
    private static SharedPreferences.Editor editor;
    private static SharedPreferences settings = null;

    static {
        editor = null;
    }

    public PassLoginStorage() {
    }

    private void init() {
        settings = context.getSharedPreferences(STORAGE_NAME, 0);
        editor = settings.edit();
    }

    public void addBooleanState(String paramString, boolean paramBoolean) {
        if (settings == null) {
            init();
        }
        editor.putBoolean(paramString, paramBoolean);
        editor.commit();
    }

    public void addString(String paramString1, String paramString2) {
        if (settings == null) {
            init();
        }
        editor.putString(paramString1, paramString2);
        editor.commit();
    }

    public boolean getBooleanState(String paramString) {
        if (settings == null) {
            init();
        }
        return settings.getBoolean(paramString, false);
    }

    public String getString(String paramString) {
        if (settings == null) {
            init();
        }
        return settings.getString(paramString, "");
    }

    public void init(Context paramContext) {
        context = paramContext;
    }
}
