package com.yatang.xc.xcr.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.X5WebView;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

/**
 * 包含H5控件，使用腾讯
 * Created by dengjiang on 2017/4/20.
 */
@ContentView(R.layout.activity_browser)
public class BrowserActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.progressBar)
    private ProgressBar progressBar;
    @BindView(id = R.id.mLayout)
    private LinearLayout mLayout;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;
    private X5WebView mWebView;

    private String mHomeUrl = "";//默认
    private int jutmpTo = 0;
    private boolean mNeedTestPage = false;

    private NomalDialog nomalDialog;
    private String marketNo;

    @Override
    public void initWidget() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow().setFlags(
                        android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                        android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
        }

        nomalDialog = new NomalDialog(aty);
        nomalDialog.setOnNoamlLickListener(onNoamlLickListener);

        //创建mWebView
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new X5WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);
        detachLayout();//取消右滑返回上一个页面
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mHomeUrl = bundle.getString("ClassUrl", "");
            marketNo = bundle.getString("marketNo", "");
            textTitle.setText(bundle.getString("ClassName", ""));
            jutmpTo = bundle.getInt("JutmpTo", 0);
        }
        if (StringUtils.isEmpty(mHomeUrl)) {
            textNoData.setVisibility(View.VISIBLE);
            return;
        } else {
            textNoData.setVisibility(View.GONE);
        }
        mTestHandler.sendEmptyMessageDelayed(MSG_INIT_UI, 10);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        switch (jutmpTo) {
            case 1:
                //跳转到主页面
                MyApplication.initCordova();
                skipActivity(aty, MainActivity.class);
                break;
            case 2:
                //跳转到登录页面
                skipActivity(aty, LoginActivity.class);
                break;
            case 3://签订合同
                nomalDialog.show("合同还未签订，确认关闭吗？");
                return;
        }
        super.onBackPressed();
    }

    NomalDialog.OnNoamlLickListener onNoamlLickListener = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            showActivity(aty, LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
        }
    };

    private void init() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                JCLoger.debug("url=" + url);
                //用腾腾讯的 tbs 没法调用此url 打开支付宝支付。 在此做一个特殊处理。
                if (url.contains("startApp") && url.contains("alipays")) {
                    try {
                        JCLoger.debug("Start intent\n" + url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        toast("请安装支付宝客户端。");
                    }
                    return true;
                }
                if (url.startsWith("xcsj://contract") && url.contains("success=1")) {
                    checkBondProgress(view);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setProgress(100);
                progressBar.setVisibility(View.GONE);
                mTestHandler.sendEmptyMessageDelayed(MSG_OPEN_TEST_URL, 5000);// 5s?
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsConfirm(WebView arg0, String arg1, String arg2, JsResult arg3) {
                return super.onJsConfirm(arg0, arg1, arg2, arg3);
            }

            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                super.onProgressChanged(webView, newProgress);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean onCreateWindow(WebView arg0, boolean b, boolean b1, Message msg) {
                WebView.WebViewTransport webViewTransport = (WebView.WebViewTransport) msg.obj;
                WebView webView = new WebView(aty) {

                    protected void onDraw(Canvas canvas) {
                        super.onDraw(canvas);
                        Paint paint = new Paint();
                        paint.setColor(Color.GREEN);
                        paint.setTextSize(15);
                        canvas.drawText("新建窗口", 10, 10, paint);
                    }
                };
                webView.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView arg0, String arg1) {
                        arg0.loadUrl(arg1);
                        return true;
                    }
                });
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(400, 600);
                lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
                mWebView.addView(webView, lp);
                webViewTransport.setWebView(webView);
                msg.sendToTarget();
                return true;
            }

            @Override
            public boolean onJsAlert(WebView arg0, String arg1, String arg2,
                                     JsResult arg3) {
                /**
                 * 这里写入你自定义的window alert
                 */
                return super.onJsAlert(null, arg1, arg2, arg3);
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String arg0, String arg1, String arg2,
                                        String arg3, long arg4) {
                new AlertDialog.Builder(BrowserActivity.this)
                        .setTitle("allow to download？")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Toast.makeText(
                                                BrowserActivity.this,
                                                "fake message: i'll download...",
                                                Toast.LENGTH_LONG).show();
                                    }
                                })
                        .setNegativeButton("no",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        Toast.makeText(
                                                BrowserActivity.this,
                                                "fake message: refuse download...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setOnCancelListener(
                                new DialogInterface.OnCancelListener() {

                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        // TODO Auto-generated method stub
                                        Toast.makeText(
                                                BrowserActivity.this,
                                                "fake message: refuse download...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
            }
        });

        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);

        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        webSetting.setSupportMultipleWindows(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);

        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        mWebView.loadUrl(mHomeUrl);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 查询合同签订进展
     */
    private void checkBondProgress(final WebView webView) {
        params.clear();
        params.put("MarketNo", marketNo);
        httpRequestService.doRequestData(aty, "System/CheckBondProgress", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    switch (Integer.parseInt(resultParam.mapData.get("ProStatue"))) {
                        case 0:
                            webView.loadUrl(resultParam.mapData.get("ContractUrl"));
                            break;
                        case 1:
                            Bundle bundle = new Bundle();
                            bundle.putInt("type", 2);
                            bundle.putInt("errCode", BaseResp.ErrCode.ERR_OK);
                            skipActivity(aty, PayResultActivity.class, bundle);
                            aty.finish();
                            break;
                    }
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (3 == jutmpTo) {
                nomalDialog.show("合同还未签订，确认关闭吗？");
                return true;
            } else if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null || mWebView == null || intent.getData() == null)
            return;
        mWebView.loadUrl(intent.getData().toString());
    }

    @Override
    protected void onDestroy() {
        if (mTestHandler != null) {
            mTestHandler.removeCallbacksAndMessages(null);
        }
        if (mWebView != null) {
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.loadUrl("about:blank");
            mWebView.stopLoading();
            mWebView.setVisibility(View.GONE);
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    public static final int MSG_OPEN_TEST_URL = 0;
    public static final int MSG_INIT_UI = 1;
    private final int mUrlStartNum = 0;
    private int mCurrentUrl = mUrlStartNum;
    private Handler mTestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OPEN_TEST_URL:
                    if (!mNeedTestPage) {
                        return;
                    }

                    String testUrl = "file:///sdcard/outputHtml/html/"
                            + Integer.toString(mCurrentUrl) + ".html";
                    if (mWebView != null) {
                        mWebView.loadUrl(testUrl);
                    }

                    mCurrentUrl++;
                    break;
                case MSG_INIT_UI:
                    init();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
