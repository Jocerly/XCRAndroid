
package com.yatang.xc.xcr.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Config;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.db.BankCardDao;
import com.yatang.xc.xcr.db.CityDao;
import com.yatang.xc.xcr.db.ProvinceDao;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ADUtils;
import com.yatang.xc.xcr.uitls.AppAutoUpdate;
import com.yatang.xc.xcr.uitls.Base64;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ModuleUpdaterManager;
import com.yatang.xc.xcr.uitls.RSAHelper;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.uitls.SerializUtil;
import com.yatang.xc.xcr.uitls.tts.SpeechUtil;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.utils.SystemTool;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 欢迎界面Activity
 *
 * @author Jocerly
 */
@ContentView(value = R.layout.activity_splash)
public class SplashActivity extends BaseActivity {
    public static final int CODE_ADDETAILS = 0X01;
    @BindView(id = R.id.imagePic)
    private ImageView imagePic;
    @BindView(id = R.id.btn_Skip)
    private TextView btn_Skip;
    @BindView(id = R.id.text_Time)
    private TextView text_Time;
    @BindView(id = R.id.lin_Skip)
    private LinearLayout lin_Skip;


    private AppAutoUpdate appAutoUpdate;
    private int getServerKeyCount = 0;// 获取公钥失败次数
    private NomalDialog dialog;
    private ProvinceDao provinceDao;
    private CityDao cityDao;
    private BankCardDao bankCardDao;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exit();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void initData() {
        dialog = new NomalDialog(aty);
        dialog.setOnNoamlLickListener(onNoamlLickListener);

        appAutoUpdate = new AppAutoUpdate(aty);
        appAutoUpdate.setOnAppUpdateClickLister(onAppUpdateClickLister);
        if (SystemTool.checkSelfPermission(aty, Manifest.permission.READ_PHONE_STATE)) {
            ActivityCompat.requestPermissions(aty, new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.Permission.READ_PHONE_STATE);
        } else {
            if (SystemTool.checkSelfPermission(aty, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(aty, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, Constants.Permission.EXTERNAL_STORAGE);
            } else {
                initialTts();
                appAutoUpdate.checkVersion(false);
                ModuleUpdaterManager.getInstance().start(); //开启cordova更新
            }
        }
    }

    /**
     * 初始化TTS引擎
     */
    private void initialTts() {
        SpeechUtil speechUtil = new SpeechUtil(aty);
        speechUtil.initTTs();
    }

    private void initADtools() {
        new ADUtils(httpRequestService, params, aty)
                .setADBtn(lin_Skip)
                .setADImg(imagePic)
                .setADText(text_Time)
                .setGetADCallBack(new ADUtils.ADCallBack() {
                    @Override
                    public void onADfinished() {
                        if (canAutoLogin()) {
                            MyApplication.initCordova();
                            skipActivity(aty, MainActivity.class);
                        } else {
                            skipActivity(aty, LoginActivity.class);
                        }
                        finish();
                    }

                    @Override
                    public void onShowAD(String url) {
                        Bundle bundle = new Bundle();
                        bundle.putString("ClassUrl", url);
                        bundle.putString("ClassName", "广告");
                        if (canAutoLogin()) {
                            bundle.putInt("JutmpTo", 1);
                        } else {
                            bundle.putInt("JutmpTo", 2);
                        }
                        skipActivity(aty, BrowserActivity.class, bundle);
                        finish();
                    }
                })
                .getADInfo();
    }

    AppAutoUpdate.OnAppUpdateClickLister onAppUpdateClickLister = new AppAutoUpdate.OnAppUpdateClickLister() {

        @Override
        public void OnCancleClickLister() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    checkBankData();
                }
            }).start();
        }
    };

    /**
     * 校验银行卡数据
     */
    private void checkBankData() {
        JCLoger.debug("checkBankData.......");
        InputStream is = null;
        provinceDao = new ProvinceDao(aty);
        cityDao = new CityDao(aty);
        bankCardDao = new BankCardDao(aty);
        if (provinceDao.getDBNum() == 0 || cityDao.getDBNum() == 0) {
            //获取Assets目录下的文件
            is = getClass().getClassLoader().getResourceAsStream("assets/bank/ProvinceAndCity.txt");
            try {
                JSONArray jsonArray = new JSONArray(Common.inputStream2String(is));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    provinceDao.doAdd(jsonObject.getString("ProvinceId"), jsonObject.getString("Province"));
                    cityDao.doAdd(jsonObject.getString("CityId"), jsonObject.getString("ProvinceId"), jsonObject.getString("City"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (bankCardDao.getDBNum() == 0) {
            //获取Assets目录下的文件
            is = getClass().getClassLoader().getResourceAsStream("assets/bank/BankCard.txt");
            try {
                JSONArray jsonArray = new JSONArray(Common.inputStream2String(is));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    bankCardDao.doAdd(jsonObject.getString("BankCardId"), jsonObject.getString("BankCardName"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mHandler.sendEmptyMessage(1);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    doIntent();
                    break;
            }
        }
    };

    NomalDialog.OnNoamlLickListener onNoamlLickListener = new NomalDialog.OnNoamlLickListener() {

        @Override
        public void onOkClick() {
            exit();
        }
    };

    @Override
    public void initWidget() {
         /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        doThreadPolicy();
        detachLayout();

        int color = getResources().getColor(R.color.white);
        setWindowColor(color);
        setDarkStatusIcon(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("首页");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("首页");
        MobclickAgent.onPause(aty);
    }

    private void doIntent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doGetServicePubKey();
            }
        }, 200);
    }

    /**
     * 获取服务器公钥
     */
    private void doGetServicePubKey() {
        try {
            params.clear();
            httpRequestService.doRequestData(this, "System/GetPublicKey", false, params, new HttpRequestService.IHttpRequestCallback() {
                @Override
                public void onRequestCallBack(ResultParam resultParam) {
                    if (Constants.M00.equals(resultParam.resultId)) {
                        String publicKey = StringUtils.bytesToString(Base64.decode(resultParam.mapData.get("PublicKey").getBytes())).replace(" ", "+");
                        Common.setAppInfo(aty, "a", SerializUtil.enSerializ(publicKey));
                        Config.serverKey = RSAHelper.decodePublicKeyFromXml(publicKey);

                        doPostDesKey();
                    } else {
                        getServerKeyCount++;
                        if (getServerKeyCount <= 3) {//获取公钥失败3次以内，重复获取
                            doGetServicePubKey();
                        } else {
                            doShowDialog();
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            doShowDialog();
        }
    }

    private void doShowDialog() {
        try {
            dialog.showClose(getResources().getString(R.string.no_connection_net));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * app提交自己的加密key到服务器
     */
    private void doPostDesKey() {
        Config.customerKey = UUID.randomUUID().toString().substring(0, 8);
        Common.setAppInfo(this, "b", SerializUtil.enSerializ(Config.customerKey));
        try {
            params.clear();
            params.put("Key", Config.customerKey);
            httpRequestService.doRequestData(this, "System/PostDesKey", false, params, new HttpRequestService.IHttpRequestCallback() {
                @Override
                public void onRequestCallBack(ResultParam resultParam) {
                    if (Constants.M00.equals(resultParam.resultId)) {
                        initADtools();
                    } else {
                        doShowDialog();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            doShowDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.Permission.EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialTts();
                    appAutoUpdate.checkVersion(false);
                    ModuleUpdaterManager.getInstance().start(); //开启cordova更新
                } else {
                    toast("获取手机存储权限失败，请到设置里面打开");
                    doIntent();
                }
                break;
            case Constants.Permission.READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    toast("获取手机设备信息权限失败，请到设置里面打开");
                }
                if (SystemTool.checkSelfPermission(aty, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(aty, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.Permission.EXTERNAL_STORAGE);
                } else {
                    initialTts();
                    appAutoUpdate.checkVersion(false);
                    ModuleUpdaterManager.getInstance().start(); //开启cordova更新
                }
                break;
        }
    }

    /**
     * 判断是否能自动登陆
     */
    private boolean canAutoLogin() {
        boolean can = false;
        MyApplication.instance.UserId = Common.getAppInfo(aty, Constants.Preference.UserId, "");
        MyApplication.instance.UserName = Common.getAppInfo(aty, Constants.Preference.UserName, "");
        MyApplication.instance.UserPhone = Common.getAppInfo(aty, Constants.Preference.UserPhone, "");
        MyApplication.instance.UserNo = Common.getAppInfo(aty, Constants.Preference.UserNo, "");
        MyApplication.instance.CityName = Common.getAppInfo(aty, Constants.Preference.CityName, "");
        MyApplication.instance.BranchOfficeId = Common.getAppInfo(aty, Constants.Preference.BranchOfficeId, "");
        MyApplication.instance.RUserInfoKey = Common.getAppInfo(aty, Constants.Preference.RUserInfoKey, "");
        MyApplication.instance.FinancialAccount = Common.getAppInfo(aty, Constants.Preference.FinancialAccount, "");
        MyApplication.instance.StoreSerialNoDefault = Common.getAppInfo(aty, Constants.Preference.StoreSerialNoDefault, "");
        MyApplication.instance.StoreSerialNameDefault = Common.getAppInfo(aty, Constants.Preference.StoreSerialNameDefault, "");
        MyApplication.instance.StoreAbbreName = Common.getAppInfo(aty, Constants.Preference.StoreAbbreName, "");
        MyApplication.instance.StoreSerialPicDefault = Common.getAppInfo(aty, Constants.Preference.StoreSerialPicDefault, "");
        MyApplication.instance.Token = Common.getAppInfo(aty, Constants.Preference.Token, "");
        MyApplication.instance.StoreNo = Common.getAppInfo(aty, Constants.Preference.StoreNo, "");
        MyApplication.instance.vipIdentify = Common.getAppInfo(aty, Constants.Preference.VipIdentify, "");
        if (!StringUtils.isEmpty(MyApplication.instance.UserId) && !StringUtils.isEmpty(MyApplication.instance.UserName)
                && !StringUtils.isEmpty(MyApplication.instance.StoreSerialNoDefault) && !StringUtils.isEmpty(MyApplication.instance.StoreSerialNameDefault) &&
                !StringUtils.isEmpty(MyApplication.instance.Token)) {
            if (!"-".equals(MyApplication.instance.Token)) {
                can = true;
            }
        }
        return can;
    }
}