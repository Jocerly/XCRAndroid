package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Config;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.MD5Utils;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录
 *
 * @author Jocerly
 */
@ContentView(value = R.layout.activity_login)
public class LoginActivity extends BaseActivity implements View.OnLayoutChangeListener {
    @BindView(id = R.id.editUserName, click = true)
    private EditText editUserName;
    @BindView(id = R.id.editPwd, click = true)
    private EditText editPwd;
    @BindView(id = R.id.butLogin, click = true)
    private TextView butLogin;
    @BindView(id = R.id.imagePwdShown, click = true)
    private ImageView imagePwdShown;
    @BindView(id = R.id.imageClear, click = true)
    private ImageView imageClear;
    @BindView(id = R.id.textForgetPwd, click = true)
    private TextView textForgetPwd;
    @BindView(id = R.id.scrollRoot)
    private ScrollView scrollRoot;
    @BindView(id = R.id.imagePwdClear, click = true)
    private ImageView imagePwdClear;
    @BindView(id = R.id.textSign, click = true)
    private TextView textSign;
    @BindView(id = R.id.login_debug_ipconfig)
    private TextView ipconfig;
    private boolean isShowPwd = false;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("登录页");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("登录页");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initData() {
        setTitle(R.string.login);
        String userName = Common.getAppInfo(aty, Constants.Preference.LoginId, "");
        editUserName.setText(userName);

        Common.setAppInfo(aty, Constants.Preference.LoginId, userName);
        MyApplication.instance.deleteLoginInfo(aty);
    }

    private void initIpConfig() {
        ipconfig.setVisibility(Config.isDebug ? View.VISIBLE : View.GONE);
        ipconfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipActivity(aty, SettingActivity.class);
            }
        });
    }

    @Override
    public void initWidget() {
        initIpConfig();
        int color = getResources().getColor(R.color.base_bg);
        setWindowColor(color);
        setDarkStatusIcon(true);
        editUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!StringUtils.isEmpty(editUserName.getText().toString().trim())) {
                        imageClear.setVisibility(View.VISIBLE);
                    } else {
                        imageClear.setVisibility(View.GONE);
                    }
                    imagePwdClear.setVisibility(View.GONE);
                } else {
                    imageClear.setVisibility(View.GONE);
                    if (!StringUtils.isEmpty(editPwd.getText().toString().trim())) {
                        imagePwdClear.setVisibility(View.VISIBLE);
                    } else {
                        imagePwdClear.setVisibility(View.GONE);
                    }
                }
            }
        });
        editUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    imageClear.setVisibility(View.VISIBLE);
                } else {
                    imageClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    imagePwdClear.setVisibility(View.VISIBLE);
                } else {
                    imagePwdClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //
        editPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String userName = editUserName.getText().toString().trim();
                    String pwd = editPwd.getText().toString().trim();
                    if (StringUtils.isEmpty(userName)) {
                        toast("请输入用户名");
                        return true;
                    }
                    if (StringUtils.isEmpty(pwd)) {
                        toast("请输入密码");
                        return true;
                    }
                    doLogin(userName, pwd);
                    return true;
                }
                return false;
            }
        });

        scrollRoot.addOnLayoutChangeListener(this);
        detachLayout();
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > 10)) {
            scrollRoot.scrollTo(0, oldBottom - bottom - 10);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.butLogin:
                String userName = editUserName.getText().toString().trim();
                String pwd = editPwd.getText().toString().trim();
                if (StringUtils.isEmpty(userName)) {
                    toast("请输入用户名");
                    return;
                }
                if (StringUtils.isEmpty(pwd)) {
                    toast("请输入密码");
                    return;
                }
                doLogin(userName, pwd);
                break;
            case R.id.imagePwdShown:
                if (isShowPwd) {
                    isShowPwd = false;
                } else {
                    isShowPwd = true;
                }
                imagePwdShown.setImageResource(isShowPwd ? R.drawable.pwd_yes : R.drawable.pwd_no);
                editPwd.setTransformationMethod(isShowPwd ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
                break;

            case R.id.textForgetPwd:
                toast("请联系运营督导反馈修改");
                break;
            case R.id.imageClear:
                editUserName.setText("");
                break;
            case R.id.imagePwdClear:
                editPwd.setText("");
                break;
            case R.id.textSign:
                skipActivity(aty, IdentityVerifyActivity.class);
                break;
        }
    }

    /**
     * 登录
     *
     * @param userName
     * @param pwd
     */
    private void doLogin(final String userName, String pwd) {
        params.clear();
        params.put("RegisterId", Common.getAppInfo(aty, Constants.Preference.Registration_Id, ""));
        params.put("LoginId", userName);
        params.put("Pwd", MD5Utils.compute(pwd));
        httpRequestService.doRequestData(aty, "System/Login", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    Common.setAppInfo(aty, Constants.Preference.LoginId, userName);
                    if (!saveData(resultParam.mapData)) {
                        skipActivity(aty, MainActivity.class);
                    } else {
                        //没有默认门店，跳转到选择默认门店页面。
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("choseDefaultStore", true);
                        skipActivity(aty, StoreListActivity.class, bundle);
                    }
                    finish();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 存储数据
     *
     * @param map
     */
    private boolean saveData(ConcurrentHashMap<String, String> map) {
        JCLoger.debug(map.toString());
        MyApplication.instance.UserId = map.get("UserId");
        MyApplication.instance.UserName = map.get("UserName");
        MyApplication.instance.UserPhone = map.get("UserPhone");
        MyApplication.instance.CityName = map.get("CityName");
        MyApplication.instance.RUserInfoKey = map.get("RUserInfoKey");
        MyApplication.instance.BranchOfficeId = map.get("BranchOfficeId");
        MyApplication.instance.UserNo = map.get("UserId");//UserId就是UserNo
        MyApplication.instance.FinancialAccount = map.get("FinancialAccount");
        MyApplication.instance.StoreSerialNoDefault = map.get("StoreSerialNoDefault");
        MyApplication.instance.StoreSerialNameDefault = map.get("StoreSerialNameDefault");
        MyApplication.instance.StoreAbbreName = map.get("StoreAbbreName");
        MyApplication.instance.StoreNo = map.get("StoreNo");
        MyApplication.instance.Token = map.get("Token");
        MyApplication.instance.vipIdentify = map.get("VipIdentify");

        Common.setAppInfo(aty, Constants.Preference.UserId, MyApplication.instance.UserId);
        Common.setAppInfo(aty, Constants.Preference.UserName, MyApplication.instance.UserName);
        Common.setAppInfo(aty, Constants.Preference.UserPhone, MyApplication.instance.UserPhone);
        Common.setAppInfo(aty, Constants.Preference.UserNo, MyApplication.instance.UserNo);
        Common.setAppInfo(aty, Constants.Preference.CityName, MyApplication.instance.CityName);
        Common.setAppInfo(aty, Constants.Preference.BranchOfficeId, MyApplication.instance.BranchOfficeId);
        Common.setAppInfo(aty, Constants.Preference.RUserInfoKey, MyApplication.instance.RUserInfoKey);
        Common.setAppInfo(aty, Constants.Preference.FinancialAccount, MyApplication.instance.FinancialAccount);
        Common.setAppInfo(aty, Constants.Preference.StoreSerialNoDefault, MyApplication.instance.StoreSerialNoDefault);
        Common.setAppInfo(aty, Constants.Preference.StoreSerialNameDefault, MyApplication.instance.StoreSerialNameDefault);
        Common.setAppInfo(aty, Constants.Preference.StoreAbbreName, MyApplication.instance.StoreAbbreName);
        Common.setAppInfo(aty, Constants.Preference.StoreNo, MyApplication.instance.StoreNo);
        Common.setAppInfo(aty, Constants.Preference.Token, MyApplication.instance.Token);
        Common.setAppInfo(aty, Constants.Preference.VipIdentify, MyApplication.instance.vipIdentify);

        MyApplication.initCordova();
        return StringUtils.isEmpty(MyApplication.instance.StoreSerialNoDefault);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
