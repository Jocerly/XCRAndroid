package com.yatang.xc.xcr.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.dialog.ReLogonDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.DataAnalyze;
import com.yatang.xc.xcr.uitls.FileUtils;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.X5WebView;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.io.File;

/**
 * html5页面展示
 * Created by Jocerly on 2017/3/20.
 */
@ContentView(R.layout.activity_webview)
public class WebViewActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.progressBar)
    private ProgressBar progressBar;
    @BindView(id = R.id.mLayout)
    private LinearLayout mLayout;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;
    private X5WebView mWebView;

    @BindView(id = R.id.ll_Finish)
    private LinearLayout ll_Finish;
    @BindView(id = R.id.btnFinish, click = true)
    private TextView btnFinish;

    private String classUrl;
    private String className;
    private String taskId;
    private String classTimes;
    private long l_time = 1000 * 60 * 5;
    private boolean loadFinished = false;
    private CountDownTimer timer;
    private String txt_times;
    private String txt_time;
    private String text_times_h;
    private ClassState state = ClassState.NOT_STUDY;
    private ValueCallback<Uri[]> mUploadCallbackAboveL = null;
    private ValueCallback<Uri> mUploadMessage = null;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("小超课堂详情");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("小超课堂详情");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        btnRight.setVisibility(View.GONE);
        text_times_h = getResources().getString(R.string.txt_left_times_hour);
        txt_times = getResources().getString(R.string.txt_left_times);
        txt_time = getResources().getString(R.string.txt_left_time);

        //创建mWebView
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new X5WebView(aty);
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);

        Bundle data = new Bundle();
//true表示标准全屏，false表示X5全屏；不设置默认false，
        data.putBoolean("standardFullScreen", false);
//false：关闭小窗；true：开启小窗；不设置默认true，
        data.putBoolean("supportLiteWnd", false);
//1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
        data.putInt("DefaultVideoScreen", 1);
        mWebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        detachLayout();//取消右滑返回上一个页面
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            classUrl = bundle.getString("ClassUrl");
            className = bundle.getString("ClassName");
            taskId = bundle.getString("TaskId");
            classTimes = bundle.getString("ClassTimes");
            if (!StringUtils.isEmpty(classTimes)) {
                l_time = Long.parseLong(classTimes) * 1000 * 60 + 1000;
            }
            if (bundle.getBoolean("IsFinished", false)) {
                state = ClassState.NOT_STUDY;
            } else {
                state = ClassState.NEED_STUDY;
            }
        }
        JCLoger.debug("url=" + classUrl);
        textTitle.setText(className);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
                } else {
                    return false;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadFinished = true;
                progressBar.setProgress(100);
                progressBar.setVisibility(View.GONE);
                if (taskId != null && state == ClassState.NEED_STUDY) {
                    initTimer();
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                super.openFileChooser(uploadMsg, acceptType, capture);

                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                String type = StringUtils.isEmpty(acceptType) ? "*/*" : acceptType;
                i.setType(type);
                startActivityForResult(Intent.createChooser(i, "File Chooser"), Constants.ForResult.FILECHOOSER_RESULTCODE);
            }

            //Android 5.0+
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mUploadCallbackAboveL != null) {
                    mUploadCallbackAboveL.onReceiveValue(null);
                }
                mUploadCallbackAboveL = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null
                        && fileChooserParams.getAcceptTypes().length > 0) {
                    i.setType(fileChooserParams.getAcceptTypes()[0]);
                } else {
                    i.setType("*/*");
                }
                startActivityForResult(Intent.createChooser(i, "File Chooser"), Constants.ForResult.FILECHOOSER_RESULTCODE);
                return true;
            }

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

            /**
             * 这里写入你自定义的window alert
             */
            @Override
            public boolean onJsAlert(com.tencent.smtt.sdk.WebView arg0, String arg1, String arg2,
                                     com.tencent.smtt.export.external.interfaces.JsResult arg3) {
                ResultParam resultParam = DataAnalyze.doAnalyze(arg2);
                if (Constants.M05.equals(resultParam.resultId)) {
                    arg3.confirm();
                    //被挤下线
                    new ReLogonDialog(aty, resultParam.message).show();
                    return true;
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    arg3.confirm();
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                    return true;
                }
                return super.onJsAlert(null, arg1, arg2, arg3);
            }

            @Override
            public boolean onJsPrompt(WebView webView, String s, String s1, String s2, JsPromptResult jsPromptResult) {
                return super.onJsPrompt(webView, s, s1, s2, jsPromptResult);
            }
        });
        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String arg0, String arg1, String arg2,
                                        String arg3, long arg4) {
                new AlertDialog.Builder(WebViewActivity.this)
                        .setTitle("allow to download？")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Toast.makeText(
                                                WebViewActivity.this,
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
                                                WebViewActivity.this,
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
                                                WebViewActivity.this,
                                                "fake message: refuse download...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
            }
        });

        com.tencent.smtt.sdk.WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);

        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        webSetting.setSupportMultipleWindows(false);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);

        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(false);
        webSetting.setDatabaseEnabled(false);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
//        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
//        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0).getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(com.tencent.smtt.sdk.WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        mWebView.loadUrl(classUrl);
        CookieSyncManager.createInstance(aty);
        CookieSyncManager.getInstance().sync();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnFinish:
                finishClass();
                break;
        }
    }

    /**
     * 移除cookie
     */
    public static void removeCookies(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        CookieSyncManager.getInstance().sync();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
//            removeCookies(aty);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.loadUrl("about:blank");
            mWebView.setVisibility(View.GONE);
            mWebView.stopLoading();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    /**
     * 完成学习
     */
    private void finishClass() {
        if (state == ClassState.STUDY_FINISH) {
            params.clear();
            params.put("UserId", MyApplication.instance.UserId);
            params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
            params.put("Token", MyApplication.instance.Token);
            params.put("ClassId", taskId);
            httpRequestService.doRequestData(aty, "User/FinishClass", params, new HttpRequestService.IHttpRequestCallback() {

                @Override
                public void onRequestCallBack(ResultParam resultParam) {
                    if (Constants.M00.equals(resultParam.resultId)) {
                        MobclickAgent.onEvent(aty, "Firm_Classes");
                        toast(getResources().getString(R.string.toast_commit_success));
                        Bundle bundle = getIntent().getExtras();
                        skipActivityForResult(aty, StudySucActivity.class, bundle, Constants.ForResult.STUDY_SUC);
                        state = ClassState.NOT_STUDY;
                        ll_Finish.setVisibility(View.GONE);
                    } else if (Constants.M01.equals(resultParam.resultId)) {
                        toast(getResources().getString(R.string.account_overdue));
                        doEmpLoginOut();
                    } else if (Constants.M06.equals(resultParam.resultId)) {
                        //任务下架
                        NomalDialog dialog = new NomalDialog(aty);
                        dialog.setOnNoamlLickListener(new NomalDialog.OnNoamlLickListener() {
                            @Override
                            public void onOkClick() {
                                finish();
                            }
                        });
                        dialog.showClose("很遗憾任务已下架！", "我知道了");

                    } else {
                        toast(resultParam.message);
                    }
                }
            });
        } else {
            toast(getResources().getString(R.string.txt_please_finish_study));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (StringUtils.isEmpty(classUrl)) {
            textNoData.setVisibility(View.VISIBLE);
            return;
        } else {
            textNoData.setVisibility(View.GONE);
        }
        switch (state) {
            case NOT_STUDY:
                ll_Finish.setVisibility(View.GONE);
                break;
            case NEED_STUDY:
                if (taskId != null && loadFinished) {
                    initTimer();
                }
                break;
            case STUDY_FINISH:
                ll_Finish.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        JCLoger.debug("onStop........");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initTimer() {
        ll_Finish.setVisibility(View.VISIBLE);
        setButtonDisable(btnFinish);
        /** 倒计时5分钟，一次1秒 */
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        setShowTime(btnFinish, l_time);
        timer = new CountDownTimer(l_time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                setShowTime(btnFinish, millisUntilFinished);
            }

            @Override
            public void onFinish() {
                btnFinish.setText(getResources().getString(R.string.btn_finish_study));
                state = ClassState.STUDY_FINISH;
                setButtonEnable(btnFinish);
            }
        }.start();
    }

    /**
     * 根据时间显示 剩余时间
     */
    private void setShowTime(View v, long times) {
        long minute = times / 1000 / 60;
        if (minute > 0) {
            long hour = minute / 60;
            if (hour > 0) {
                long second = (times - minute * 60 * 1000) / 1000;
                minute = minute - hour * 60;
                btnFinish.setText(String.format(text_times_h, hour, minute, second));
            } else {
                long second = (times - minute * 60 * 1000) / 1000;
                btnFinish.setText(String.format(txt_times, minute, second));
            }
        } else {
            btnFinish.setText(String.format(txt_time, times / 1000));
        }
        l_time = times;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        mWebView.destroy();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ForResult.STUDY_SUC:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;

            case Constants.ForResult.FILECHOOSER_RESULTCODE:
                if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                if (result == null) {
                    if (mUploadCallbackAboveL != null) {
                        mUploadCallbackAboveL.onReceiveValue(null);
                    } else if (mUploadMessage != null) {
                        mUploadMessage.onReceiveValue(null);
                    }
                    mUploadCallbackAboveL = null;
                    mUploadMessage = null;
                    return;
                }

                String path = FileUtils.getPath(this, result);
                JCLoger.debug(path);
                if (StringUtils.isEmpty(path)) {
                    if (mUploadCallbackAboveL != null) {
                        mUploadCallbackAboveL.onReceiveValue(null);
                    } else if (mUploadMessage != null) {
                        mUploadMessage.onReceiveValue(null);
                    }
                    mUploadCallbackAboveL = null;
                    mUploadMessage = null;
                    return;
                }
                Uri uri = Uri.fromFile(new File(path));

                if (mUploadCallbackAboveL != null) {
                    mUploadCallbackAboveL.onReceiveValue(new Uri[]{uri});
                } else if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(uri);
                }
                mUploadCallbackAboveL = null;
                mUploadMessage = null;
                break;
        }
    }

    private void setButtonDisable(TextView btn) {
        btn.setBackgroundResource(R.drawable.btn_recircle_text_light2);
        btn.setTextColor(getResources().getColor(R.color.white));

    }

    private void setButtonEnable(TextView btn) {
        btn.setBackgroundResource(R.drawable.btn_recircle_red_bg);
        btn.setTextColor(getResources().getColor(R.color.white));
    }

    /**
     * 课程状态
     *
     * @NOT_STUDY 不是学习任务
     * @NOT_STUDY 学习任务初始状态
     * @STUDY_FINISH, 学习完成未提交状态
     */
    private enum ClassState {
        NOT_STUDY,
        NEED_STUDY,
        STUDY_FINISH,
    }
}
