package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.yatang.plugin.navigation.CordovaPageActivity;
import com.yatang.xc.xcr.BuildConfig;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Config;
import com.yatang.xc.xcr.uitls.ToolPreference;

import org.jocerly.jcannotation.utils.JCLoger;

/**
 * Created by zengxiaowen on 2017/7/24.
 */

public class SettingActivity extends PreferenceActivity {
    public static final String KEY_HOST_PREF = "key_host_pref";
    public static final String KEY_TS_HOST_PREF = "key_ts_host_pref";

    private ListPreference listPreference,supLoadWay;
    private EditTextPreference host, ts_host,supServer;
    public static final String HOST_DEV_KEY = "host_dev_key";
    public static final String HOST_TS_DEV_KEY = "host_ts_dev_key";
    public static final String HOST_TEST_KEY = "host_test_key";
    public static final String HOST_TS_TEST_KEY = "host_ts_test_key";
    public static final String HOST_PRO_KEY = "host_pro_key";
    public static final String HOST_TS_PRO_KEY = "host_ts_pro_key";
    public static final String HOST_TYPE_KEY = "host_type_key";
    public static final String SUP_TYPE_KEY = "sup_load_way";
    public static final String SUP_SERVER_KEY = "sup_server";
    public static final String ENABLE_GLOBAL_CAPTURE_KEY = "global_capture";
    public static final String ENABLE_AUTO_FILL_ATTCHMENT_KEY = "auto_fill_attachment";
    public static final String ENABLE_AUTO_FILL_ATTCHMENT_PATH_KEY = "auto_fill_attachment_path";

    private String host_dev, host_ts_dev, host_test, host_ts_test, host_pro, host_ts_pro, type,sup_load_way,sup_server;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_activity);

        host_dev = ToolPreference.get().getString(HOST_DEV_KEY);
        host_ts_dev = ToolPreference.get().getString(HOST_TS_DEV_KEY);
        host_test = ToolPreference.get().getString(HOST_TEST_KEY);
        host_ts_test = ToolPreference.get().getString(HOST_TS_TEST_KEY);
        host_pro = ToolPreference.get().getString(HOST_PRO_KEY);
        host_ts_pro = ToolPreference.get().getString(HOST_TS_PRO_KEY);
        sup_load_way = ToolPreference.get().getString(SUP_TYPE_KEY);
        sup_server = ToolPreference.get().getString(SUP_SERVER_KEY);

        if (host_dev == null || host_dev.isEmpty()) {
            host_dev = BuildConfig.DEBUG ? BuildConfig.CLIENT_HOST : Config.NetSitConfig.request_url;
        }
        if (host_ts_dev == null || host_ts_dev.isEmpty()) {
            host_ts_dev = BuildConfig.DEBUG ? BuildConfig.OnTag : Config.NetSitConfig.OnTag;
        }
        if (host_test == null || host_test.isEmpty()) {
            host_test = Config.NetUatConfig.request_url;
        }
        if (host_ts_test == null || host_ts_test.isEmpty()) {
            host_ts_test = Config.NetUatConfig.OnTag;
        }
        if (host_pro == null || host_pro.isEmpty()) {
            host_pro = !BuildConfig.DEBUG ? BuildConfig.CLIENT_HOST : Config.NetProConfig.request_url;
        }
        if (host_ts_pro == null || host_ts_pro.isEmpty()) {
            host_ts_pro = !BuildConfig.DEBUG ? BuildConfig.OnTag : Config.NetProConfig.OnTag;
        }


        type = ToolPreference.get().getString(HOST_TYPE_KEY);

        listPreference = (ListPreference) getPreferenceManager().findPreference("env_selection");
        supLoadWay = (ListPreference) getPreferenceManager().findPreference("sup_load_way");
        host = (EditTextPreference) getPreferenceManager().findPreference("host");
        ts_host = (EditTextPreference) getPreferenceManager().findPreference("ts_host");
        supServer = (EditTextPreference) getPreferenceManager().findPreference("sup_server");

        JCLoger.debug("Setting:" + ToolPreference.get().getString("env_selection"));


        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                type = newValue.toString();
                JCLoger.debug("newValue:" + type);
                ToolPreference.get().putString(HOST_TYPE_KEY, type);
                listPreference.setSummary(getSummary());
                setEdit(type);
                return true;
            }
        });

        host.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                host.setSummary(newValue.toString());
                switch (type) {
                    case "dev":
                        ToolPreference.get().putString(HOST_DEV_KEY, newValue.toString());
                        break;
                    case "test":
                        ToolPreference.get().putString(HOST_TEST_KEY, newValue.toString());
                        break;
                    case "produce":
                        ToolPreference.get().putString(HOST_PRO_KEY, newValue.toString());
                        break;
                }
                return true;
            }
        });

        supServer.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                supServer.setText(newValue.toString());
                supServer.setSummary(newValue.toString());
                ToolPreference.get().putString(SUP_SERVER_KEY, newValue.toString());
                CordovaPageActivity.setBaseUrl(newValue.toString());
                return true;
            }
        });

        ts_host.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ts_host.setSummary(newValue.toString());
                switch (type) {
                    case "dev":
                        ToolPreference.get().putString(HOST_TS_DEV_KEY, newValue.toString());
                        break;
                    case "test":
                        ToolPreference.get().putString(HOST_TS_TEST_KEY, newValue.toString());
                        break;
                    case "produce":
                        ToolPreference.get().putString(HOST_TS_PRO_KEY, newValue.toString());
                        break;
                }
                return true;
            }
        });

        supLoadWay.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ToolPreference.get().putString(SUP_TYPE_KEY,newValue.toString());
                if("manual".equals(newValue.toString())){
                    supLoadWay.setSummary("手动");
                    CordovaPageActivity.setIsManual(true);
                }else{
                    supLoadWay.setSummary("默认");
                    CordovaPageActivity.setIsManual(false);
                }
                return true;
            }
        });


        if(sup_load_way != null && "manual".equals(sup_load_way)){
            supLoadWay.setDefaultValue("manual");
            supLoadWay.setSummary("手动");
        }else{
            supLoadWay.setDefaultValue("default");
            supLoadWay.setSummary("默认");
        }

        if(sup_server != null && !sup_server.trim().isEmpty()){
            supServer.setText(sup_server);
            supServer.setSummary(sup_server);
        }else{
            supServer.setText(CordovaPageActivity.getBaseUrl());
            supServer.setSummary(CordovaPageActivity.getBaseUrl());
        }

        if (type != null) {
            listPreference.setValue(type);
            listPreference.setSummary(getSummary());
            setEdit(type);
        } else {
            type = BuildConfig.DEBUG ? "dev" : "produce";
            listPreference.setValue(type);
            listPreference.setSummary(getSummary());
            setEdit(type);
        }
    }

    private void setEdit(String type) {
        switch (type) {
            case "dev":
                host.setSummary(host_dev);
                host.setText(host_dev);
                ts_host.setSummary(host_ts_dev);
                ts_host.setText(host_ts_dev);
                break;
            case "test":
                host.setSummary(host_test);
                host.setText(host_test);
                ts_host.setSummary(host_ts_test);
                ts_host.setText(host_ts_test);
                break;
            case "produce":
                host.setSummary(host_pro);
                host.setText(host_pro);
                ts_host.setSummary(host_ts_pro);
                ts_host.setText(host_ts_pro);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Config.NetConfig.request_url = host.getText();
        Config.NetConfig.OnTag = ts_host.getText();
        ToolPreference.get().putString(KEY_HOST_PREF, Config.NetConfig.request_url);
        ToolPreference.get().putString(KEY_TS_HOST_PREF, Config.NetConfig.OnTag);

        restartApplication();
    }

    private String getSummary() {
        switch (type) {
            case "dev":
                return "sit环境";
            case "test":
                return "uat环境";
            case "produce":
                return "测试生产环境";
            default:
                return "选择当前需要访问的环境配置";
        }
    }

    private void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}