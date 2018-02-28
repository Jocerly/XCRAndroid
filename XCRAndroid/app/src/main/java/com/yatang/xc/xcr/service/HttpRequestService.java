package com.yatang.xc.xcr.service;

import android.app.Service;
import android.content.Context;

import com.baidu.tts.tools.DeviceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Config;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.ReLogonDialog;
import com.yatang.xc.xcr.uitls.Base64;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.DESEncrype;
import com.yatang.xc.xcr.uitls.DataAnalyze;
import com.yatang.xc.xcr.uitls.RSAHelper;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.uitls.SerializUtil;

import org.jocerly.jcannotation.ui.JCActivityStack;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.utils.SystemTool;
import org.jocerly.jcannotation.widget.UIHelper;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * service类
 *
 * @author Jocerly
 */
public class HttpRequestService {
    private AsyncHttpClient asyncHttpClient;//http请求类
    private String url = null;

    public HttpRequestService() {
        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.setTimeout(Constants.MAXTIMEOUT);
        }
    }

    /**
     * 具体执行的方法
     */
    public void doRequestData(Context context, String method, HashMap<String, Object> params, IHttpRequestCallback requestCallback) {
        doRequestData(context, method, true, params, requestCallback);
    }

    /**
     * 具体执行的方法
     */
    public void doRequestData(Context context, String method, boolean isShowDialog, HashMap<String, Object> params, IHttpRequestCallback requestCallback) {
        JSONObject jsonObject = null;
        jsonObject = new JSONObject(params);
        String msg = jsonObject.toString();
        try {
            RequestParams requestParams = new RequestParams();

            if ("System/PostDesKey".equals(method)) {
                if (Config.serverKey == null) {
                    Config.serverKey = RSAHelper.decodePublicKeyFromXml(SerializUtil.deSerializ(Common.getAppInfo(context, "a", "")).toString());
                }
                String message = URLEncoder.encode(StringUtils.bytesToString(Base64.encode(RSAHelper.encryptData(msg.getBytes(), Config.serverKey))), "utf-8");
                requestParams.put("msg", message);
            } else if ("System/Login".equals(method) || "User/SetBankCardMsg".equals(method)) {//需要加密的接口
                if (!StringUtils.isEmpty(msg)) {
                    if (StringUtils.isEmpty(Config.customerKey)) {
                        Config.customerKey = SerializUtil.deSerializ(Common.getAppInfo(context, "b", "b")).toString();
                    }
                    JCLoger.debug(msg);
                    String message = DESEncrype.encryptDES(msg, Config.customerKey);
                    requestParams.put("msg", message);
                }
            } else {//不需要加密
                requestParams.put("msg", msg);
            }
            doExcute(context, isShowDialog, requestParams, method, msg, requestCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求方法
     */
    public void doExcute(final Context context, final boolean isShowDialog, final RequestParams requestParams, final String method, final String msg, final IHttpRequestCallback requestCallback) {
        url = Config.NetConfig.request_url + method;
        if (StringUtils.isEmpty(MyApplication.instance.macAddress)) {
            MyApplication.instance.macAddress = DeviceId.getDeviceID(context);
//            MyApplication.instance.macAddress = Common.getCid(context);
        }
        if (StringUtils.isEmpty(MyApplication.instance.version)) {
            MyApplication.instance.version = SystemTool.getAppVersionName(context);
        }

        String cookie = "DeviceId=" + MyApplication.instance.macAddress + ",Type=1,AppVersion=" + MyApplication.instance.version;
        JCLoger.debug("cookie:" + cookie);
        asyncHttpClient.addHeader("Cookie", cookie);//865335024316147
        asyncHttpClient.post(context, url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                JCLoger.debug(method + "========" + url + "?" + requestParams.toString());
                if (isShowDialog) {
                    try {
                        if ("System/GetPublicKey".equals(method) || "System/PostDesKey".equals(method)) {
                            UIHelper.showLoadDialog(context, true);
                        } else {
                            UIHelper.showLoadDialog(context);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                try {
                    if (requestCallback != null) {
                        doClose();
                        String result = new String(arg2, "utf-8");
                        ResultParam resultParam = DataAnalyze.doAnalyze(context, method, result);
//                        if ("System/Login".equals(method) || "User/SetBankCardMsg".equals(method)) {
//                            if (StringUtils.isEmpty(Config.customerKey)) {
//                                Config.customerKey = SerializUtil.deSerializ(Common.getAppInfo(context, "b", "b")).toString();
//                            }
//                            result = DESEncrype.decryptDES(result, Config.customerKey);
//                        }
//                        ResultParam resultParam = DataAnalyze.doAnalyze(result);
                        if (method.startsWith("User") && Constants.M05.equals(resultParam.resultId)) {
                            if (context instanceof Service) {
                                new ReLogonDialog(JCActivityStack.create().topActivity(), resultParam.message).show();
                            } else {
                                new ReLogonDialog(context, resultParam.message).show();
                            }
                        } else {
                            requestCallback.onRequestCallBack(resultParam);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                doClose();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                doClose();
                if (requestCallback != null) {
                    JCLoger.debug("请求失败");
                    ResultParam resultParam = ResultParam.getInstance();
                    resultParam.resultId = Constants.M98;
                    resultParam.message = context.getResources().getString(R.string.no_connection_net);
                    requestCallback.onRequestCallBack(resultParam);
                }
            }

            @Override
            public void onCancel() {
                super.onCancel();
            }

            private void doClose() {
                UIHelper.cloesLoadDialog();
            }
        });
    }

    /**
     * http调用完毕后，异步返回结果操作接口
     *
     * @author Jocerly
     */
    public interface IHttpRequestCallback {
        void onRequestCallBack(ResultParam resultParam);
    }

    /**
     * 装填参数
     *
     * @param map 参数
     * @return
     */
    public static RequestParams fillParms(Map<String, ?> map, String charset) {
        JCLoger.debug("参数：" + map.toString());
        RequestParams params = new RequestParams();
//        params.put()
        if (null != map && map.entrySet().size() > 0) {
            // 设置字符集,防止参数提交乱码
            if (!"".equals(charset)) {
                params.setContentEncoding(charset);
            }
            for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry entity = (Map.Entry) iterator.next();
                Object key = entity.getKey();
                Object value = entity.getValue();
                if (value instanceof File) {
                    try {
                        params.put((String) key, new FileInputStream((File) value), ((File) value).getName());
                    } catch (FileNotFoundException e) {
//                        throw new RuntimeException("文件不存在！", e);
                        JCLoger.debug("文件不存在！");
                    }
                } else if (value instanceof InputStream) {
                    params.put((String) key, (InputStream) value);
                } else {
                    params.put((String) key, value == null ? "" : value.toString());
                }
            }

        }
        return params;
    }
}
