package com.yatang.xc.xcr.uitls;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zengxiaowen on 2017/7/24.
 */

public class ToolPreference {
    private static SharedPreferences preference;

    private static final String YT_XCTZ_PNAME = "yt_xcr_pref";

    private static ToolPreference instance = new ToolPreference();

    private ToolPreference() {

    }

    public void init(Context context) {
        preference = context.getSharedPreferences(YT_XCTZ_PNAME, Context.MODE_PRIVATE);
    }

    public static ToolPreference get() {
        return instance;
    }

    public boolean putString(String key, String value) {
        if (preference == null) return false;
        return preference.edit().putString(key, value).commit();
    }

    public boolean putBoolean(String key, boolean value) {
        if (preference == null) return false;
        return preference.edit().putBoolean(key, value).commit();
    }

    public String getString(String key) {
        if (preference == null) return null;
        return preference.getString(key, null);
    }

    public boolean getBoolean(String key) {
        if (preference == null) return false;
        return preference.getBoolean(key, false);
    }
}
