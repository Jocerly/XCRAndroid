package com.yatang.xc.xcr.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.service.HttpRequestService;

import org.jocerly.jcannotation.ui.JCFragment;
import org.jocerly.jcannotation.ui.ViewInject;
import org.jocerly.jcannotation.utils.JCLoger;

import java.util.HashMap;

/**
 * 本地Fragment基类
 *
 * @author asus
 */
@SuppressLint("NewApi")
public abstract class BaseFragment extends JCFragment {
    protected HttpRequestService httpRequestService;
    protected HashMap<String, Object> params;
    protected Activity aty;
    protected JCFragment currentJCFragment;
    protected Handler handler = null;

    @Override
    protected void initData() {
        super.initData();
        aty = getActivity();
        httpRequestService = new HttpRequestService();
        params = new HashMap<String, Object>();
        handler = new Handler();
    }

    protected void toast(String msg) {
        try {
            ViewInject.toast(aty, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void toast(int msgId) {
        try {
            ViewInject.toast(aty, getResources().getString(msgId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        currentJCFragment = null;
        aty = null;
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
    public void showActivity(Context context, Class<?> cls) {
        JCLoger.debug(context.getClass().getName());
        Intent intent = new Intent();
        intent.setClass(context, cls);
        startActivity(intent);
        aty.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    /**
     * Activity跳转
     */
    public void showActivityForResult(Context context, Class<?> cls, int requestCode) {
        JCLoger.debug(context.getClass().getName());
        Intent intent = new Intent();
        intent.setClass(context, cls);
        startActivityForResult(intent, requestCode);
        aty.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    /**
     * show to @param(cls)，but can't finish activity
     */
    public void showActivityForResult(Context context, Class<?> cls, Bundle extras, int requestCode) {
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(context, cls);
        startActivityForResult(intent, requestCode);
        aty.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
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
        aty.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
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
        aty.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
}
