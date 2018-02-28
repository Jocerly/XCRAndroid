package com.yatang.cordova.navigation;

import org.apache.cordova.CallbackContext;

/**
 * Created by liaoqinsen on 2017/7/27 0027.
 */

public interface CordovaPage {
    public void setCallBack(int callbackId,CallbackContext callBack);
    public void setResult(String data);
    public int getCallBackId();
    public String getUrl();
    public void funcJS(String funcName,String params);
    public CordovaPageActivity getActivity();
}
