package com.yatang.xc.supchain;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.yatang.xc.supchain.uitls.JCLoger;

import org.apache.cordova.CordovaActivity;

/**
 * 基类，创建的实例全部继承于该类
 * Created by Jocerly on 2017/7/13.
 */

public class BaseActivity extends CordovaActivity {
    private String name, data;
    private int in, out;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.init();
        if (getIntent() != null && getIntent().getExtras() != null) {
            name = getIntent().getExtras().getString("name");
            in = getIntent().getExtras().getInt("in");
            out = getIntent().getExtras().getInt("out");
            data = getIntent().getExtras().getString("data");
        }
        String path = null;
        if (getIntent() != null && getIntent().getExtras() != null) {
            path = getIntent().getExtras().getString("path");
        }
        if (ActivityManager.isFirst()) {
            path = "file:///android_asset/www/index.html";
        }

        JCLoger.debug(path);
        if (path != null) {
            loadUrl(path);
            View view = appView.getView();
            if (view instanceof WebView) {
                ((WebView) view).getSettings().setUseWideViewPort(true);
            }
        } else {
            android.widget.Toast.makeText(this, "找不到HTML文件", android.widget.Toast.LENGTH_SHORT).show();
            ActivityManager.goBack();
        }

        ActivityManager.current(this);
    }

    public String getData() {
        return data;
    }

    @Override
    public void finish() {
        ActivityManager.goBack();
    }

    public void finishByManager() {
        if (in > 0 && out > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finishInternal();
                    overridePendingTransition(in, out);
                }
            });
        } else {
            finishInternal();
        }
    }

    private void finishInternal() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        ActivityManager.goBack();
    }
}
