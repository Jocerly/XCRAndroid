package com.yatang.cordova.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.yatang.xc.supchain.R;

import org.apache.cordova.AppCompatCordovaActivity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.x5engine.X5WebViewClient;
import org.apache.cordova.x5engine.X5WebViewEngine;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liaoqinsen on 2017/6/22 0022.
 */

public class CordovaPageActivity extends AppCompatCordovaActivity implements CordovaPage {

    private String name, data, path;

    //是否需要隐藏标题栏
    private boolean hideStatusBar = true;

    private int callbackId = -1;

    //存储cordova前端js的回调实例，用于activity间传递数据时向前端页面传递数据
    private CallbackContext callbackContext;

    //检查http链接的正则表达式
    private String check = "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";

    private int in = R.anim.slide_int_left, out = R.anim.slide_out_left;

    public static void setToken(String token) {
        CordovaPageActivity.token = token;
    }

    public static void setShopId(String shopId) {
        CordovaPageActivity.shopId = shopId;
    }

    public static void setBaseUrl(String baseUrl) {
        CordovaPageActivity.baseUrl = baseUrl;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBranchOfficeId(String branchOfficeId) {
        CordovaPageActivity.branchOfficeId = branchOfficeId;
    }

    public static String getNewVersion() {
        return newVersion;
    }

    public static String getNewModuleId() {
        return newModuleId;
    }

    public static void setCityName(String cityName) {
        CordovaPageActivity.cityName = cityName;
    }

    public static void setConfigPage(JSONObject configPage) {
        CordovaPageActivity.configPage = configPage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    //    private static String token, shopId, branchOfficeId, baseUrl = "https://xcscapp.yatang.com.cn/";
    private static String token, shopId, branchOfficeId, baseUrl = "http://sitxcsc.yatang.com.cn/";

    private String adUrl = null;

    public static final String zipStorePath = "zip";
    public static final String rootStorePath = "cordova";
    private static String newVersion;
    private static String newModuleId;
    private static String cityName;
    private static JSONObject configPage;

    //测试用，是否使用手动配置的供应链存储地址，用于前端工程师测试时替换供应链web工程文件
    private static boolean isManual = false;

    public static void setIsManual(boolean isManual) {
        CordovaPageActivity.isManual = isManual;
    }

    public static void setNewVersion(String newVersion) {
        CordovaPageActivity.newVersion = newVersion;
    }

    public static void setNewModuleId(String newModuleId) {
        CordovaPageActivity.newModuleId = newModuleId;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String getPath(Context context) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + context.getPackageName();
    }


    public static String getZipPath(Context context, String moduleId, String version, boolean containName) {
        return getPath(context) + File.separator + rootStorePath + File.separator + zipStorePath + File.separator + (containName ? (moduleId + "_" + version + ".zip") : "");
    }

    public static String getWebpackPath(Context context, String moduleId, String version) {
        return getPath(context) + File.separator + rootStorePath + File.separator + moduleId + File.separator + version + File.separator;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null && getIntent().getExtras() != null) {
            name = getIntent().getExtras().getString("path");
            path = getIntent().getExtras().getString("path");
            in = getIntent().getExtras().getInt("in");
            out = getIntent().getExtras().getInt("out");
            data = getIntent().getExtras().getString("data");
            hideStatusBar = getIntent().getExtras().getBoolean("hide", true);
            adUrl = getIntent().getExtras().getString("AdJump");
        }

        //读取恢复数据
        if (savedInstanceState != null) {
            String tmpPath = savedInstanceState.getString("path");
            if (tmpPath != null) {
                path = tmpPath;
            }
        }

        if (getSupportActionBar() != null) {
            if (!hideStatusBar) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().show();
            } else {
                getSupportActionBar().hide();
            }
        }

        //如果需要手动替换文件则将供应链web存放地址默认为sd卡根目录下的www文件夹
        if (isManual) {
            if (path == null || Navigator.isFirst()) {
                path = "file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/www/" + "index.html";
                name = "index.html";
            } else {
                if (!path.startsWith("http")) {
                    path = "file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/www/" + path;
                }
            }
        } else {
            //如果路径为空或者首次进入供应链模块，则页面地址为index.html
            if (path == null || Navigator.isFirst()) {
                Navigator.clearAll();
                path = "file://" + getWebpackPath(this, newModuleId, newVersion) + "index.html" + "?cityName=\"" + (cityName == null ? "" : cityName) + "\"";
                name = "index.html";
            } else {
                //如果path是http地址，则为外部链接，不需要处理
                if (!path.startsWith("http") && !path.startsWith("https")) {
                    path = "file://" + getWebpackPath(this, newModuleId, newVersion) + path;
                }
            }

            //处理广告链接跳转
            if (adUrl != null) {
                URI url;
                try {
                    url = new URI(adUrl);
                    String content = url.getPath().split("/")[1];
                    //获取模块id
                    String moduleId = url.getHost();
                    //获取跳转参数
                    String query = url.getQuery();
                    if (configPage != null) {
                        path = "file://" + getWebpackPath(this, moduleId, newVersion) + configPage.optString(content, "index.html") + "?" + query;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (path != null) {
            loadUrl(path);
            View view = appView.getView();
            if (view instanceof WebView) {
                //前端请求拦截
                ((WebView) view).setWebViewClient(new X5WebViewClient((X5WebViewEngine) appView.getEngine()) {


                    @Override
                    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                        int resCode;
                        String resPhrase;
                        StringBuilder stringBuilder = new StringBuilder();
                        BufferedReader bufferedReader = null;
                        Map<String, List<String>> map = null;
                        try {
                            Log.d("corodova_activity", "origin url" + request.getUrl());
                            //前端传过来的请求是不完整的http链接（e.g:http://index/query?a=123,b=234）,因此当url以http开头，但是不是完整的http链接时才需要进行拦截
                            if (request.getUrl().toString().startsWith("http") && !request.getUrl().toString().matches(check)) {
                                String[] u = request.getUrl().toString().split("//");
                                if (u == null || u.length < 2) {
                                    return super.shouldInterceptRequest(view, request);
                                }
                                String urlString = u[1];
                                String postBody = null;
                                if ("POST".equalsIgnoreCase(request.getMethod())) {
                                    String[] postBodys = URLDecoder.decode(u[1], "utf-8").split("\\?postBody=");
                                    if (postBodys != null && postBodys.length > 0) {
                                        urlString = postBodys[0];
                                        Log.d("lqs", "url=" + urlString);
                                    }
                                    if (postBodys != null && postBodys.length > 1) {
                                        postBody = postBodys[1];
                                    }
                                }
                                URL url = new URL(baseUrl + urlString);

                                Log.d("corodova_activity", "url:" + url);
                                Log.d("corodova_activity", "token:" + token);
                                Log.d("corodova_activity", "shopid:" + shopId);
                                Log.d("corodova_activity", "branchOfficeId:" + branchOfficeId);
                                Log.d("corodova_activity", "method:" + request.getMethod());
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setRequestMethod(request.getMethod());
                                if (!"POST".equalsIgnoreCase(request.getMethod()) || !"GET".equals(request.getMethod())) {
                                    httpURLConnection.setRequestMethod("GET");
                                }
                                httpURLConnection.setConnectTimeout(10 * 1000);
                                httpURLConnection.setReadTimeout(40 * 1000);
                                Map<String, String> headers = request.getRequestHeaders();
                                Log.d("corodova_activity","request:"+request.toString());
                                for (String key : headers.keySet()) {
                                    httpURLConnection.setRequestProperty(key, headers.get(key));
                                }
                                httpURLConnection.setRequestProperty("token", token);
                                httpURLConnection.setRequestProperty("shopNum", shopId);
                                httpURLConnection.setRequestProperty("branchOfficeId", branchOfficeId);
                                if (postBody != null) {
                                    DataOutputStream out = new DataOutputStream(
                                            httpURLConnection.getOutputStream());
                                    out.writeChars(postBody);
                                    out.flush();
                                    out.close();
                                }
                                resCode = httpURLConnection.getResponseCode();
                                resPhrase = httpURLConnection.getResponseMessage();
                                map = httpURLConnection.getHeaderFields();

                                Log.d("corodova_activity", "resCode:" + resCode);
                                Log.d("corodova_activity", "MSG:" + resPhrase);
                                try {
                                    InputStream inputStream = httpURLConnection.getInputStream();
                                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                                } catch (Exception e) {
                                    InputStream errorStream = httpURLConnection.getErrorStream();
                                    bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
                                }
                                String line = "";
                                while ((line = bufferedReader.readLine()) != null)
                                    stringBuilder.append(line);
                            } else {
                                return super.shouldInterceptRequest(view, request);
                            }
                        } catch (Exception e) {
                            Log.d("corodova_activity", "ERROR:" + e.getMessage());
                            e.printStackTrace();
                            return super.shouldInterceptRequest(view, request);
                        } finally {
                            if (bufferedReader != null)
                                try {
                                    bufferedReader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        }
                        WebResourceResponse webResourceResponse = null;
                        String mimeType = "text/plain";
                        String enCoding = "utf-8";
                        Map<String, String> resMap = new HashMap<String, String>();
                        //获取http返回结果的MIME类型和编码类型用于组装webResourceResponse
                        if (map != null) {
                            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                                if (entry.getValue() != null && entry.getValue().size() > 0) {
                                    resMap.put(entry.getKey(), entry.getValue().get(0));
                                    if ("Content-Type".equalsIgnoreCase(entry.getKey())) {
                                        String[] value = entry.getValue().get(0).split(";");
                                        for (String v : value) {
                                            if (v.contains("/")) {
                                                mimeType = v;
                                            }
                                            if (v.startsWith("charset")) {
                                                String[] chasets = v.split("=");
                                                if (chasets.length > 1) {
                                                    enCoding = chasets[1];
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        webResourceResponse = new WebResourceResponse(mimeType, enCoding, new ByteArrayInputStream(stringBuilder.toString().getBytes()));
                        webResourceResponse.setStatusCodeAndReasonPhrase(resCode, resPhrase);
                        webResourceResponse.setResponseHeaders(resMap);
                        return webResourceResponse;
                    }
                });
            }
        } else {
            android.widget.Toast.makeText(this, "找不到HTML文件", android.widget.Toast.LENGTH_SHORT).show();
            Navigator.goBack(null);
        }

        Navigator.current(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("path", name);
    }

    public String getData() {
        return data;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (callbackId >= 0 && requestCode == callbackId && callbackContext != null && intent != null) {
            String data = intent.getStringExtra("data");
            List<PluginResult> pluginResultList = new ArrayList<>();
            pluginResultList.add(new PluginResult(PluginResult.Status.OK, callbackId));
            pluginResultList.add(new PluginResult(PluginResult.Status.OK, data));
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, pluginResultList);
            callbackContext.sendPluginResult(pluginResult);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void finish() {
        Navigator.goBack(null);
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

    public void finishInternal() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        Navigator.goBack(null);
    }

    @Override
    public void setCallBack(int callbackId, CallbackContext callBack) {
        this.callbackId = callbackId;
        this.callbackContext = callBack;
    }

    @Override
    public void setResult(String data) {
        if (callbackId >= 0 && callbackContext != null) {
            List<PluginResult> pluginResultList = new ArrayList<>();
            pluginResultList.add(new PluginResult(PluginResult.Status.OK, callbackId));
            pluginResultList.add(new PluginResult(PluginResult.Status.OK, data));
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, pluginResultList);
            callbackContext.sendPluginResult(pluginResult);
        }

    }

    @Override
    public int getCallBackId() {
        return callbackId;
    }

    @Override
    public String getUrl() {
        return name;
    }

    @Override
    public void funcJS(String funcName, String params) {
        if (funcName == null) return;
        appView.sendJavascript("javascript:" + funcName + "(" + (params == null ? "" : params) + ")");
    }

    @Override
    public CordovaPageActivity getActivity() {
        return this;
    }


    private interface ResultHandler {
        public boolean handleResult(String data);
    }
}
