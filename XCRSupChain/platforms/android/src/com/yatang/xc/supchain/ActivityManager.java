package com.yatang.xc.supchain;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.cordova.CallbackContext;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * 类管理器
 * Created by Jocerly on 2017/7/13.
 */

public class ActivityManager {
    public static final LinkedList<BaseActivity> activitys;
    public static final LinkedHashMap<BaseActivity, CallbackContext> callbackContexts;
    private static BaseActivity current;
    private static Handler handler;

    private static CallbackContext callbackContext;

    public static void registerGlobalNotfiy(CallbackContext call) {
        callbackContext = call;
    }

    static {
        activitys = new LinkedList<BaseActivity>();
        callbackContexts = new LinkedHashMap<BaseActivity, CallbackContext>();
    }

    public static void current(BaseActivity activity) {
        if (current != null) {
            activitys.push(current);
        }
        current = activity;
    }

    public static String currentData() {
        return current.getData();
    }

    public static boolean isFirst() {
        return current == null;
    }

    public static void init() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
    }

    public static void skip(Class<BaseActivity> activityClass) {
        skip(activityClass, null);
    }

    public static void skip(Class<BaseActivity> activityClass, Bundle bundle) {
        if (current == null) return;
        Intent intent = new Intent(current, activityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        current.startActivity(intent);
    }


    public static void skip(String name, final Bundle bundle, CallbackContext call) {
        if (current == null) return;
        callbackContexts.put(current, call);
        if (name != null) {
            boolean has = false;
            for (BaseActivity BaseActivity : activitys) {
                if (name.equals(BaseActivity.getName())) {
                    has = true;
                    break;
                }
            }
            if (has) {
                while (activitys.size() > 0) {
                    if (name.equals(current.getName())) {
                        String data = bundle.getString("data");
                        if (data != null) {
                            CallbackContext callbackContext = callbackContexts.get(current);
                            if (callbackContext != null) callbackContext.success(data);
                        }
                        return;
                    }
                    current.finishByManager();
                    callbackContexts.remove(current);
                    current = activitys.pop();
                }
                return;
            }
        }
        final Intent intent = new Intent(current, BaseActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        String anim = bundle.getString("anim");
        if (anim != null) {
            /*switch (anim) {
                case "right":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            intent.putExtra("in", R.anim.slide_in_right);
                            intent.putExtra("out", R.anim.slide_out_right);
                            current.startActivity(intent);
                            current.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        }
                    });
                    break;
                case "bottom":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            intent.putExtra("in", R.anim.slide_in_bottom);
                            intent.putExtra("out", R.anim.slide_out_bottom);
                            current.startActivity(intent);
                            current.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                        }
                    });
                    break;
                default:
                    current.startActivity(intent);
                    break;
            }*/
        } else {
            current.startActivity(intent);
        }
    }

    public static void skipForResult(Class<BaseActivity> activityClass, Bundle bundle, CallbackContext callbackContext) {
        if (current == null) return;
        Intent intent = new Intent(current, activityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        callbackContexts.put(current, callbackContext);
        current.startActivityForResult(intent, 100);
    }

    public static void goBack() {
        goBack(null);
    }

    public static void goBack(Bundle data) {
        Log.d("lqs","0");
        if (current == null) return;
        Log.d("lqs","1");
        current.finishByManager();
        Log.d("lqs","2");
        callbackContexts.remove(current);
        Log.d("lqs","3");
        if (activitys.size() > 0) {
            current = activitys.pop();
        } else {
            current = null;
        }
        if (data == null) return;
        CallbackContext callbackContext = callbackContexts.get(current);
        String d = data.getString("data");
        if (d != null && callbackContext != null) {
            callbackContext.success(d);
        }
    }
}
