package com.yatang.plugin.ytlogicservice;


import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This class echoes a string called from JavaScript.
 */
public class YTLogicService extends CordovaPlugin {

    private static LogicServiceGetter getter;
    private static LogicServiceErrorHandler errorHandler;

    public static void setGetter(LogicServiceGetter getter) {
        YTLogicService.getter = getter;
    }

    public static void setErrorHandler(LogicServiceErrorHandler errorHandler) {
        YTLogicService.errorHandler = errorHandler;
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
        if(action.equals("fetchCommonParams") && getter != null){
            callbackContext.success(getter.get());
        }else if("handleError".equals(action)){
            if(errorHandler != null) {
                errorHandler.handlerError(args.optString(0, ""));
            }
        }
        return true;
    }

    public interface LogicServiceGetter{
        public JSONObject get();
    }

    public interface LogicServiceErrorHandler{
        public void handlerError(String errorCode);
    }
}
