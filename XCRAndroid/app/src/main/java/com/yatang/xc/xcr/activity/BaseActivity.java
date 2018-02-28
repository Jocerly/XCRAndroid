/*
 * Copyright (c) 2014, Jocerly.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yatang.xc.xcr.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.PicassoImageLoader;
import com.yatang.xc.xcr.uitls.PicassoPauseOnScrollListener;
import com.yatang.xc.xcr.uitls.tts.SpeechUtil;
import com.yatang.xc.xcr.views.SwipeBackLayout;

import org.jocerly.jcannotation.ui.AnnotateUtil;
import org.jocerly.jcannotation.ui.I_JCActivity;
import org.jocerly.jcannotation.ui.JCActivityStack;
import org.jocerly.jcannotation.ui.JCFragment;
import org.jocerly.jcannotation.ui.ViewInject;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.utils.SystemTool;

import java.util.HashMap;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;

/**
 * 基类
 *
 * @author Jocerly
 */
@SuppressLint("NewApi")
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, I_JCActivity {
    public Activity aty;
    protected View contentView;//正文view

    public HttpRequestService httpRequestService;
    public HashMap<String, Object> params;

    protected boolean isExit = false;// 记录退出状态
    protected JCFragment currentFragment;
    protected int colorGap;
    protected SwipeBackLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        aty = this;
        //子类绑定ui
        AnnotateUtil.initBindView(aty);
        super.onCreate(savedInstanceState);
        initValue();
        JCActivityStack.create().addActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        attachLayout();
        setWindowColor(getResources().getColor(R.color.red));
        initWidget();
        initData();
    }

    /**
     * 将滑动销毁的layout设置给activity
     */
    protected void attachLayout() {
        layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
                R.layout.activity_base, null);
        layout.attachToActivity(this);
    }

    /**
     * 将滑动销毁的layout从activity移除
     */
    protected void detachLayout() {
        if (layout != null) {
            layout.detachToActivity(this);
            layout = null;
        }
    }

    Handler handler = new Handler();

    @Override
    public void onClick(View v) {
    }

    public void exit() {
        JCLoger.debug("exit........");
        SpeechUtil speechUtil = new SpeechUtil(aty);
        speechUtil.onDestroy();
        JCActivityStack.create().AppExit(getApplicationContext());
    }

    /**
     * 防止网络在主线程中不能访问
     */
    public void doThreadPolicy() {
        if (SystemTool.getSDKVersion() > 8) {
            //防止网络在主线程中不能访问
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void initValue() {
        params = new HashMap<String, Object>();
        httpRequestService = MyApplication.instance.getHttpRequestService();

        colorGap = getResources().getColor(R.color.line);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JCLoger.debug(aty.getClass().getName() + "---------onDestroy ");
        JCActivityStack.create().finishActivity(this);
        currentFragment = null;
        aty = null;
    }

    public void toast(String msg) {
        try {
            if (!StringUtils.isEmpty(msg)) {
                ViewInject.toast(aty, msg);
            } else {
                ViewInject.toast(aty, "加载失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void longToast(String msg) {
        try {
            if (!StringUtils.isEmpty(msg)) {
                ViewInject.longToast(aty, msg);
            } else {
                ViewInject.longToast(aty, "加载失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void toast(int msgId) {
        try {
            if (msgId != 0) {
                ViewInject.toast(aty, getResources().getString(msgId));
            } else {
                ViewInject.toast(aty, "加载失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Activity跳转
     */
    public void skipActivity(Context context, Class<?> cls) {
        showActivity(context, cls);
    }

    /**
     * Activity跳转
     */
    public void skipActivity(Context context, Intent it) {
        showActivity(context, it);
    }

    /**
     * Activity跳转
     */
    public void skipActivity(Context context, Class<?> cls, Bundle extras) {
        showActivity(context, cls, extras);
    }

    /**
     * Activity跳转
     */
    public void skipActivityForResult(Context context, Class<?> cls, Bundle extras, int requestCode) {
        showActivityForResult(context, cls, extras, requestCode);
    }

    /**
     * Activity跳转
     */
    public void skipActivityForResult(Context context, Class<?> cls, int requestCode) {
        showActivityForResult(context, cls, requestCode);
    }

    /**
     * Activity跳转
     */
    public void showActivity(Context context, Class<?> cls) {
        JCLoger.debug(context.getClass().getName());
        Intent intent = new Intent();
        intent.setClass(context, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public void finish() {
        super.finish();
        if (!(aty instanceof SplashActivity) && !(aty instanceof LoginActivity)) {
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    }

    /**
     * Activity跳转
     */
    public void showActivityForResult(Context context, Class<?> cls, int requestCode) {
        JCLoger.debug(context.getClass().getName());
        Intent intent = new Intent();
        intent.setClass(context, cls);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    /**
     * show to @param(cls)，but can't finish activity
     */
    public void showActivityForResult(Context context, Class<?> cls, Bundle extras, int requestCode) {
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(context, cls);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    /**
     * Activity跳转
     */
    public void showActivity(Context context, Class<?> cls, int flag) {
        JCLoger.debug(context.getClass().getName());
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.addFlags(flag);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    /**
     * Activity跳转
     */
    public void showActivity(Context context, Class<?> cls, Bundle extras, int flag) {
        JCLoger.debug(context.getClass().getName());
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtras(extras);
        intent.setFlags(flag);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    /**
     * Activity跳转
     */
    public void showActivity(Context context, Intent it) {
        startActivity(it);
    }

    /**
     * show to @param(cls)，but can't finish activity
     */
    public void showActivity(Context context, Class<?> cls, Bundle extras) {
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(context, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    /**
     * 登出
     */
    public void doEmpLoginOut() {
        MyApplication.instance.clearData();
        showActivity(aty, LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
        aty.finish();
    }

    /**
     * 用Fragment替换视图
     *
     * @param layoutId       将要被替换掉的视图
     * @param targetFragment 用来替换的Fragment
     */
    public void changeFragment(int layoutId, JCFragment targetFragment) {
        if (targetFragment.equals(currentFragment)) {
            return;
        }
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction.add(layoutId, targetFragment, targetFragment.getClass().getName());
        }
        if (currentFragment != null && currentFragment.isVisible()) {
            transaction.hide(currentFragment);
        }
        if (targetFragment.isHidden()) {
            transaction.show(targetFragment);
            transaction.remove(currentFragment);//移除当前Fragment，下次打开需要从新初始化
        }
        currentFragment = targetFragment;
        transaction.commit();
    }

    public static FunctionConfig initGalleryFinal(Context mContext) {
        //设置主题
        ThemeConfig theme = new ThemeConfig.Builder()
                .setTitleBarBgColor(mContext.getResources().getColor(R.color.red))
                .setIconBack(R.drawable.back_bg)
                .build();
        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(false)//相机
                .setEnableEdit(false) //编辑
                .setEnableCrop(false) //裁剪
                .setEnableRotate(false) //开启选择功能
                .setCropSquare(false) ////裁剪正方形
                .setEnablePreview(true)//是否开启预览功能
//                .setMutiSelectMaxSize(1)//配置多选数量
                .build();
        CoreConfig coreConfig = new CoreConfig.Builder(mContext, new PicassoImageLoader(), theme)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(new PicassoPauseOnScrollListener(false, true))
                .build();
        GalleryFinal.init(coreConfig);
        return functionConfig;
    }

    /**
     * 对号码条进行check
     */
    protected boolean isCodeOK(String str) {
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            }
        }
        String regex = "^[a-zA-Z0-9]+$";
        boolean isRight = isDigit && str.matches(regex);
        return isRight;
    }

    /**
     * 1位小数
     */
    TextWatcher textWatcher1 = new TextWatcher() {
        public void afterTextChanged(Editable edt) {
            String temp = edt.toString();
            int posDot = temp.indexOf(".");
            if (posDot <= 0) return;
            if (temp.length() - posDot - 1 > 1) {
                edt.delete(posDot + 2, posDot + 3);
            }
        }

        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };

    /**
     * 2位小数
     */
    TextWatcher textWatcher2 = new TextWatcher() {
        public void afterTextChanged(Editable edt) {
            String temp = edt.toString();
            int posDot = temp.indexOf(".");
            if (posDot <= 0) return;
            if (temp.length() - posDot - 1 > 2) {
                edt.delete(posDot + 3, posDot + 4);
            }
        }

        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };

    public void setWindowColor(int windowColor) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = aty.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(windowColor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置状态栏图标为黑色或者白色（其实按照本意，是告诉系统状态栏顶部是白色的，需要按一个合适的模式去绘制状态栏，当然，其实就是黑色）。
     * @param bDark
     */
    public void setDarkStatusIcon(boolean bDark) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            View decorView = getWindow().getDecorView();
            if(decorView != null){
                int vis = decorView.getSystemUiVisibility();
                if(bDark){
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else{
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }
}
