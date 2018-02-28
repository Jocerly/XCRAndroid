package com.yatang.xc.xcr.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.ContractSettledActivity;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.X5WebView;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

/**
 * 合同签订
 * Created by Jocerly on 2017/11/1.
 */
public class ContractSignFragment extends BaseFragment {
    public static final int MSG_INIT_UI = 1;

    @BindView(id = R.id.mLayout)
    private LinearLayout mLayout;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;
    private X5WebView mWebView;

    private String contractUrl;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_contract_sign, null);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        contractUrl = ((ContractSettledActivity) getActivity()).getContractUrl();
        if (StringUtils.isEmpty(contractUrl)) {
            textNoData.setVisibility(View.VISIBLE);
            mLayout.setVisibility(View.GONE);
            return;
        } else {
            textNoData.setVisibility(View.GONE);
            mLayout.setVisibility(View.VISIBLE);
        }

        //创建mWebView
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new X5WebView(aty);
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);

        mTestHandler.sendEmptyMessageDelayed(MSG_INIT_UI, 10);
    }

    private Handler mTestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT_UI:
                    init();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void init() {
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                JCLoger.debug(url);
                if (url.startsWith("xcsj://contract") && url.contains("success=1")) {
                    checkBondProgress(webView);
                    return true;
                } else {
                    return false;
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                super.onProgressChanged(webView, newProgress);

                JCLoger.debug("newProgress.........."+newProgress);
            }
        });

        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
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
        webSetting.setAppCachePath(aty.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(aty.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(aty.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);

        mWebView.loadUrl(contractUrl);
        CookieSyncManager.createInstance(aty);
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 查询合同签订进展
     */
    private void checkBondProgress(final WebView webView) {
        params.clear();
        params.put("MarketNo", ((ContractSettledActivity) getActivity()).getMarketNo());
        httpRequestService.doRequestData(aty, "System/CheckBondProgress", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    switch (Integer.parseInt(resultParam.mapData.get("ProStatue"))) {
                        case 0:
                            contractUrl = resultParam.mapData.get("ContractUrl");
                            webView.loadUrl(contractUrl);
                            break;
                        case 1:
                            ((ContractSettledActivity) getActivity()).setStep(3);
                            break;
                    }
                } else if (Constants.M13.equals(resultParam.resultId)) {//加盟超时
                    ((ContractSettledActivity) getActivity()).showTinmeOutDialog();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
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
}
