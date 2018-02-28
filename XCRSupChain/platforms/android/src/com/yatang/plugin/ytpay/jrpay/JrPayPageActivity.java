package com.yatang.plugin.ytpay.jrpay;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yatang.plugin.ytpay.YTPay;

/**
 * Created by liuping on 2017/11/13.
 * <p>
 * 余额支付
 */

public class JrPayPageActivity extends AppCompatActivity {
    public static final String REMAININGPAY = "JrPayReq";
    LinearLayout container;
    JrPayReq jrPayReq;

    X5WebView mWebView;
//    String url = "http://192.168.1.7:8080/payApi/indexh5.html";

    @Override
    protected void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initData();
        initView();
        initWebView();
        loadPayWebview();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void initWebView() {

        mWebView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                super.onProgressChanged(webView, newProgress);
                if (newProgress == 100) {
                    mWebView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
                return super.onJsAlert(webView, s, s1, jsResult);
            }
        });

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        mWebView.setWebViewClient(new WebViewClient() {


            /**
             * 防止加载网页时调起系统浏览器
             */
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onReceivedError(WebView webView, int i, String s, String s1) {
                super.onReceivedError(webView, i, s, s1);
                 Toast.makeText(JrPayPageActivity.this, "服务器开小差", Toast.LENGTH_SHORT).show();
                 JrPayPageActivity.this.finish();
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {

              if(request.getUrl().toString().startsWith(jrPayReq.sync_url)) {

                    Uri uri = request.getUrl();
                    String status = uri.getQueryParameter("status");
                    JrPayResp jrPayResp = new JrPayResp();
                    jrPayResp.status = status;
                    //todo 解析支付结果
                    YTPay.setJrPayResult(jrPayResp);
                    finish();
                    return null;
                }

                if(request.getUrl().toString().startsWith(jrPayReq.async_url)) {

                    Uri uri = request.getUrl();
                    String status = uri.getQueryParameter("status");
                    JrPayResp jrPayResp = new JrPayResp();
                    jrPayResp.status = status;
                    //todo 解析支付结果
                    YTPay.setJrPayResult(jrPayResp);
                    finish();
                    return null;
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
    }
    private void initData() {
        if (getIntent() == null) {
            Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Intent intent = getIntent();
        jrPayReq = (JrPayReq) intent.getSerializableExtra(REMAININGPAY);
        if(jrPayReq == null ){
            Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

    }

    private void loadPayWebview() {
        mWebView.postUrl(jrPayReq.getApiUrl(), jrPayReq.parserRequsetParam().getBytes());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mWebView != null){
            mWebView.onResume();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mWebView != null){
            mWebView.onPause();
        }
    }

    @NonNull
    private void initView() {
        //1.instane  container

        container = new LinearLayout(this);
        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        container.setLayoutParams(containerParams);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        //2.instane webview
        LinearLayout.LayoutParams webViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        mWebView = new X5WebView(getApplicationContext());
        mWebView.setId(200);
        mWebView.requestFocusFromTouch();
        mWebView.setLayoutParams(webViewParams);

        //3. add webview
        container.addView(mWebView);
        setContentView(container);
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
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        destoryWebview();
        super.onDestroy();
    }

    private void destoryWebview() {
        if (mWebView != null) {
            mWebView.setWebViewClient(null);
            mWebView.setWebChromeClient(null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        finish();
    }
}
