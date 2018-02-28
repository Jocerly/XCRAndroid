package com.yatang.plugin.ytnotification;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class echoes a string called from JavaScript.
 */
public class YTNotification extends CordovaPlugin {

    private static final HashMap<String,CallbackContext> callBackContexts = new HashMap<>();
    private static final HashMap<String,String> callbackIds = new HashMap<>();

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        return super.execute(action, args, callbackContext);
    }

    @Override
    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) throws JSONException {
        return super.execute(action, rawArgs, callbackContext);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("emit".equals(action)) {
            String id = args.optString(0);
            String data = args.optString(1);
            CallbackContext callback = callBackContexts.get(id);
            String callBackId = callbackIds.get(id);
            if(callback != null){
                List<PluginResult> pluginResultList = new ArrayList<>();
                pluginResultList.add(new PluginResult(PluginResult.Status.OK, callBackId));
                pluginResultList.add(new PluginResult(PluginResult.Status.OK, data));
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, pluginResultList);
                pluginResult.setKeepCallback(true);
                callback.sendPluginResult(pluginResult);
            }
        }else if("listen".equals(action)){
            String id = args.optString(0);
            String callbackId = args.optString(1);
            callBackContexts.put(id,callbackContext);
            callbackIds.put(id,callbackId);
        }
        return true;
    }
}
