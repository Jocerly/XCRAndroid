package com.yatang.cordova.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.yatang.xc.supchain.R;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by liaoqinsen on 2017/7/27 0027.
 *
 * 页面跳转插件的跳转器
 */

public class Navigator {
    public static final LinkedList<CordovaPage> pages;
    public static CordovaPage current;
    private static final Handler handler;

    static {
        pages = new LinkedList<>();
        handler = new Handler(Looper.getMainLooper());
    }

    public static void clearAll(){
        if(current != null) {
            current.getActivity().finish();
            current = null;
        }
        for(CordovaPage cordovaPage:pages){
            cordovaPage.getActivity().finish();
        }
        pages.clear();
    }


    public static boolean isFirst(){
        return current == null;
    }

    public static void current(CordovaPage cordovaPage){
        if(current != null && current.getActivity() != null && !current.getActivity().isFinishing() && !current.getActivity().isDestroyed()) {
            pages.push(current);
        }
        current = cordovaPage;
    }


    public static void redirect(String url,String anim, boolean closeSelf,boolean hideStatusBar){
        if(current == null) return;
        final Intent intent = new Intent(current.getActivity(),CordovaPageActivity.class);
        intent.putExtra("path",url);
        intent.putExtra("hide",hideStatusBar);
        CordovaPageActivity waiting4Close = current.getActivity();
        if(anim != null) {
            switch (anim) {
                case "right":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            intent.putExtra("in", R.anim.slide_in_right);
                            intent.putExtra("out", R.anim.slide_out_right);
                            current.getActivity().startActivity(intent);
                            current.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        }
                    });
                    break;
                case "bottom":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            intent.putExtra("in", R.anim.slide_in_bottom);
                            intent.putExtra("out", R.anim.slide_out_bottom);
                            current.getActivity().startActivity(intent);
                            current.getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                        }
                    });
                    break;
                default:
                    current.getActivity().startActivity(intent);
                    break;
            }
        }else{
            current.getActivity().startActivity(intent);
        }

        if(closeSelf){
            waiting4Close.finishInternal();
        }
    }

    public static void redirect(String url, final int callbackId, String anim, boolean closeSelf,boolean hideStatusBar, CallbackContext callbackContext){
        if(current == null) return;
        final Intent intent = new Intent(current.getActivity(),CordovaPageActivity.class);
        Log.d("lqs1","id="+callbackId+"callbakc="+callbackContext);
        intent.putExtra("path",url);
        intent.putExtra("hide",hideStatusBar);
        current.setCallBack(callbackId,callbackContext);
        CordovaPageActivity waiting4Close = current.getActivity();
        if(anim != null) {
            switch (anim) {
                case "right":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            intent.putExtra("in", R.anim.slide_int_left);
                            intent.putExtra("out", R.anim.slide_out_left);
                            current.getActivity().startActivityForResult(intent,callbackId);
                            current.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        }
                    });
                    break;
                case "bottom":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            intent.putExtra("in", R.anim.slide_in_bottom);
                            intent.putExtra("out", R.anim.slide_out_bottom);
                            current.getActivity().startActivityForResult(intent,callbackId);
                            current.getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                        }
                    });
                    break;
                default:
                    current.getActivity().startActivityForResult(intent,callbackId);
                    break;
            }
        }else{
            current.getActivity().startActivityForResult(intent,callbackId);
        }

        if(closeSelf){
            waiting4Close.finishInternal();
        }
    }

    public static void popPage(boolean popSelf, Activity activity){
        if(activity == null || current == null || pages.size() < 1) return;
        List<CordovaPage> tmpPages = new ArrayList<>();
        tmpPages.addAll(pages);
        boolean hasPage = false;
        if(popSelf && current.getActivity().equals(activity)){
            goBack(null);
            return;
        }
        for(CordovaPage cordovaPage:pages){
            if(activity.equals(cordovaPage.getActivity())){
                hasPage = true;
                break;
            }
        }
        if(!hasPage) return;
        if(activity.equals(current.getActivity())){
            if(popSelf) {
                goBack(null);
            }
            return;
        }else{
            goBack(null);
        }

        do{
            try{
                if(activity.equals(current.getActivity())){
                    if(!popSelf){
                        return;
                    }else{
                        goBack(null);
                        return;
                    }
                }
                goBack(null);
            }catch (Exception e){
                current = null;
            }
        }while (current != null);
    }

    public static List<CordovaPage> getAllPages(){
        List<CordovaPage> tmpList = new ArrayList<>();
        tmpList.addAll(pages);
        return tmpList;
    }

    public static void popAllPage(){
        while (current != null){
            goBack(null);
        }
    }

    public static void goBack(String data){
        if(current == null) return;
        current.getActivity().finishByManager();
        if(pages.size() > 0) {
            current = pages.pop();
            if(data != null) {
                current.setResult(data);
            }
        }else{
            current = null;
        }
    }
}
