package com.yatang.plugin.ytnetwork;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class YTNetwork extends CordovaPlugin {

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
        if ("checkStatus".equals(action)) {
            int i = isAvailable();
            callbackContext.success(""+i);
        }
        return true;
    }

    public int isAvailable() {
        ConnectivityManager manager = (ConnectivityManager) cordova.getActivity()
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (null == manager) {
            return 0;
        }
        try {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if ((null == networkInfo || !networkInfo.isAvailable())) {
                return 0;
            } else if (ConnectivityManager.TYPE_MOBILE == networkInfo.getType()) {
                return 2;
            } else if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
                return 1;
            } else {
                return networkInfo.getType();
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

}
