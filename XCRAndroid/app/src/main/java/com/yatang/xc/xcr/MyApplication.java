package com.yatang.xc.xcr;

import android.app.Activity;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.baidu.mapapi.SDKInitializer;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yatang.plugin.navigation.CordovaPageActivity;
import com.yatang.plugin.navigation.Navigator;
import com.yatang.plugin.ytlogicservice.YTLogicService;
import com.yatang.plugin.ytpay.YTPay;
import com.yatang.xc.xcr.activity.LoginActivity;
import com.yatang.xc.xcr.activity.SettingActivity;
import com.yatang.xc.xcr.config.Config;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.service.X5UpDateService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResolutionUtil;
import com.yatang.xc.xcr.uitls.ToolNetwork;
import com.yatang.xc.xcr.uitls.ToolPreference;

import org.jocerly.jcannotation.utils.SystemTool;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

import static com.yatang.xc.xcr.activity.SettingActivity.SUP_SERVER_KEY;

public class MyApplication extends MultiDexApplication {
    public static MyApplication instance;
    private HttpRequestService httpRequestService;

    public String UserId = "";
    public String UserName = "";//加盟商名字
    public String UserPhone = "";
    public String UserNo = "";//加盟商编号
    public String CityName = "";//城市名
    public String RUserInfoKey = "";//用户信息key
    public String BranchOfficeId = "";//分公司ID
    public String FinancialAccount = "";//金融帐号
    public String StoreSerialNoDefault = "";
    public String StoreSerialNameDefault = "";
    public String StoreAbbreName = "";//门店简称
    public String StoreNo = "";//店铺编号（只是展示作用）
    public String StoreSerialPicDefault = "";
    public String Token = "";
    public String macAddress = "";
    public String version = "";
    public String vipIdentify = "";

    public boolean isX5Over = false;

    public static IWXAPI api;
    /**
     * 1:保证金，2:供应链
     */
    public int payType = 2;
    public String marketNo = "";
    public String bondValue = "";
    public String phone = "";
    public String token_id = "";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        version = SystemTool.getAppVersionName(instance);

        initIP();
        initX5();
        initJPush();

        CrashReport.initCrashReport(getApplicationContext(), Constants.CrashReportId, false);
        SDKInitializer.initialize(getApplicationContext());
        ResolutionUtil.getInstance().init(this);
    }

    private void initJPush() {
        JPushInterface.setDebugMode(Config.isDebug);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        Set<String> stringSet = new HashSet<>();//设置tag
        stringSet.add(Config.NetConfig.OnTag);
        JPushInterface.setTags(getApplicationContext(), stringSet, null);
    }

    private void initX5() {
        api = WXAPIFactory.createWXAPI(this, YTPay.APP_ID);
        Intent intent = new Intent(instance, X5UpDateService.class);
        startService(intent);
    }

    private void initIP() {
        ToolPreference.get().init(this);
        String hostValue = ToolPreference.get().getString(SettingActivity.KEY_HOST_PREF);
        String tsHostValue = ToolPreference.get().getString(SettingActivity.KEY_TS_HOST_PREF);
        if (hostValue != null && !hostValue.isEmpty()) {
            Config.NetConfig.request_url = hostValue;
        }
        if (tsHostValue != null && !tsHostValue.isEmpty()) {
            Config.NetConfig.OnTag = tsHostValue;
        }
    }

    /**
     * 声明对象
     *
     * @return
     */
    public HttpRequestService getHttpRequestService() {
        if (httpRequestService == null) {
            httpRequestService = new HttpRequestService();
        }
        return httpRequestService;
    }

    /**
     * 获取网络是否已连接
     *
     * @return
     */
    public static boolean isNetworkReady() {
        return ToolNetwork.getInstance().init(instance).isConnected();
    }

    public static void initCordova() {
        if ("manual".equals(ToolPreference.get().getString(SettingActivity.SUP_TYPE_KEY))) {
            CordovaPageActivity.setIsManual(true);
        }

        String sup_server = ToolPreference.get().getString(SUP_SERVER_KEY);
        if (sup_server != null && !sup_server.trim().isEmpty()) {
            CordovaPageActivity.setBaseUrl(sup_server);
        }
        CordovaPageActivity.setToken(MyApplication.instance.RUserInfoKey);
        CordovaPageActivity.setCityName(MyApplication.instance.CityName);
        CordovaPageActivity.setShopId(MyApplication.instance.StoreSerialNoDefault);
        CordovaPageActivity.setBranchOfficeId(MyApplication.instance.BranchOfficeId);
        YTLogicService.setGetter(new YTLogicService.LogicServiceGetter() {
            @Override
            public JSONObject get() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userName", MyApplication.instance.StoreSerialNameDefault);
                    jsonObject.put("cityName", MyApplication.instance.CityName);
                    jsonObject.put("userId", MyApplication.instance.UserId);
                    jsonObject.put("avatarUrl", null);
                    jsonObject.put("ip", Common.getIPAddress(MyApplication.instance));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }
        });
        YTLogicService.setErrorHandler(new YTLogicService.LogicServiceErrorHandler() {
            @Override
            public void handlerError(String errorCode) {
                if ("401".equals(errorCode)) {
                    if (Navigator.current != null && Navigator.current.getActivity() != null) {
//                        ReLogonDialog reLogonDialog = new ReLogonDialog(Navigator.current.getActivity(),null);
//                        reLogonDialog.show();
                        MyApplication.instance.clearData();
                        Intent intent = new Intent(Navigator.current.getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Navigator.current.getActivity().startActivity(intent);
                        Navigator.clearAll();
                    }
                }
            }
        });

        YTPay.setIwxapi(api);
    }

    public void clearData() {
        MyApplication.instance.UserId = "";
        MyApplication.instance.UserName = "";
        MyApplication.instance.UserPhone = "";
        MyApplication.instance.UserNo = "";
        MyApplication.instance.CityName = "";
        MyApplication.instance.RUserInfoKey = "";
        MyApplication.instance.FinancialAccount = "";
        MyApplication.instance.StoreSerialNoDefault = "";
        MyApplication.instance.StoreSerialNameDefault = "";
        MyApplication.instance.StoreAbbreName = "";
        MyApplication.instance.StoreNo = "";
        MyApplication.instance.StoreSerialPicDefault = "";
        MyApplication.instance.Token = "";
        MyApplication.instance.BranchOfficeId = "";
    }

    public void deleteLoginInfo(Activity aty) {
        Common.setAppInfo(aty, Constants.Preference.UserId, "-");
        Common.setAppInfo(aty, Constants.Preference.UserName, "-");
        Common.setAppInfo(aty, Constants.Preference.UserNo, "-");
        Common.setAppInfo(aty, Constants.Preference.FinancialAccount, "-");
        Common.setAppInfo(aty, Constants.Preference.StoreSerialNoDefault, "-");
        Common.setAppInfo(aty, Constants.Preference.StoreSerialNameDefault, "-");
        Common.setAppInfo(aty, Constants.Preference.StoreAbbreName, "-");
        Common.setAppInfo(aty, Constants.Preference.StoreNo, "-");
        Common.setAppInfo(aty, Constants.Preference.StoreSerialPicDefault, "-");
        Common.setAppInfo(aty, Constants.Preference.Token, "-");
    }
}
