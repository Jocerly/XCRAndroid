package com.yatang.plugin.ytpay.jrpay;

import android.content.Context;

import com.tencent.smtt.sdk.WebView;

/**
 * Created by liuping on 2017/11/15.
 */
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;

import java.util.Map;

/**
 * Created by dengjiang on 2017/4/20.
 */

public class X5WebView extends WebView {

    public X5WebView(Context arg0) {
        super(arg0);
        initWebViewSettings();
        this.getView().setClickable(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        // this.setWebChromeClient(chromeClient);
        // WebStorage webStorage = WebStorage.getInstance();
        initWebViewSettings();
        this.getView().setClickable(true);
    }

    public X5WebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public X5WebView(Context context, AttributeSet attributeSet, int i, boolean b) {
        super(context, attributeSet, i, b);
    }

    public X5WebView(Context context, AttributeSet attributeSet, int i, Map<String, Object> map, boolean b) {
        super(context, attributeSet, i, map, b);
    }

    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
//        webSetting.setGeolocationEnabled(true);
        webSetting.setPluginState(WebSettings.PluginState.ON);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setPluginsEnabled(true);


    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean ret = super.drawChild(canvas, child, drawingTime);
        return ret;
    }


}