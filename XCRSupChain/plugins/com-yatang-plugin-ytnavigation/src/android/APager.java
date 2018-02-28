package com.yatang.cordova.navigation;

import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 导航页面管理
 * This class echoes a string called from JavaScript.
 *
 * 页面导航插件
 */
public class APager extends CordovaPlugin {
    private static CordovaActivity aty;

    public static void init(CordovaActivity activity) {
        APager.aty = activity;
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        return super.execute(action, args, callbackContext);
    }

    @Override
    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) throws JSONException {
        return super.execute(action,rawArgs,callbackContext);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d("lqs",action+":"+args.toString());
        if ("redirect".equals(action)) {
            String url = args.getString(0);
            JSONObject jsonObject = null;
            try{
                jsonObject = args.getJSONObject(1);
            }catch (Exception e){

            }
            if(jsonObject != null){
                Navigator.redirect(url,jsonObject.optInt("paramsCallBackId",-1),jsonObject.optString("transition"),jsonObject.optBoolean("closeSelf",false),jsonObject.optBoolean("navigationBarHidden",true),callbackContext);
            }else{
                Navigator.redirect(url,"right",false,true);
            }
        } else if ("goBack".equals(action)) {
            String jsonObject = null;
            try{
                jsonObject = args.getString(0);
            }catch (Exception e){

            }
            if(jsonObject == null){
                Navigator.goBack(null);
            }else{
                Navigator.goBack(jsonObject);
            }
        } else if ("popPage".equals(action)) {
            boolean popSelf = args.optBoolean(0);
            Navigator.popPage(popSelf,cordova.getActivity());
        } else if ("popAllPage".equals(action)) {
            Navigator.popAllPage();
        } else {
            if (action.equals("deepLinkRedirect")) {
                String url = args.getString(0);
                JSONObject jsonObject = null;
                try {
                    jsonObject = args.getJSONObject(0);
                } catch (Exception e) {

                }
                Bundle bundle = new Bundle();
                bundle.putString("data", jsonObject == null ? null : jsonObject.toString());
            }
        }
        return true;
    }
}
