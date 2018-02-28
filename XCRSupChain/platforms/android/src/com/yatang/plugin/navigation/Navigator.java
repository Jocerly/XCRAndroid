package com.yatang.plugin.navigation;

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

    /**
     * 清空并关闭所有activity
     */
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


    /**
     * 判断当前activity是否是第一个启动的CordovaPageActivity
     * @return
     */
    public static boolean isFirst(){
        return current == null;
    }

    /**
     * 每次CordovaPageActivity create时都要调用该方法来将自身存入栈中
     * @param cordovaPage
     */
    public static void current(CordovaPage cordovaPage){
        if(current != null && current.getActivity() != null && !current.getActivity().isFinishing() && !current.getActivity().isDestroyed()) {
            pages.push(current);
        }
        current = cordovaPage;
    }

    /**
     * 跳转接口
     * @param moduleId 需要显示的页面模块ID
     * @param url 需要显示的页面url
     * @param anim 页面切换动画效果
     * @param closeSelf 是否关闭自己
     * @param hideStatusBar 是否隐藏原生标题栏
     */
    public static void deepLinkRedirect(String moduleId,String url,String anim, boolean closeSelf,boolean hideStatusBar){
        if(current == null) return;
        final Intent intent = new Intent(current.getActivity(),CordovaPageActivity.class);
        intent.putExtra("path",url);
        intent.putExtra("hide",hideStatusBar);
        intent.putExtra("moduleId",moduleId);
        intent.putExtra("deepLinkRedirect",true);
        CordovaPageActivity waiting4Close = current.getActivity();
        startActivity(anim, intent);
        if(closeSelf){
            waiting4Close.finishInternal();
        }
    }

    /**
     * 跳转接口
     * @param url 需要显示的页面url
     * @param anim 页面切换动画效果
     * @param closeSelf 是否关闭自己
     * @param hideStatusBar 是否隐藏原生标题栏
     */
    public static void redirect(String url,String anim, boolean closeSelf,boolean hideStatusBar){
        if(current == null) return;
        final Intent intent = new Intent(current.getActivity(),CordovaPageActivity.class);
        intent.putExtra("path",url);
        intent.putExtra("hide",hideStatusBar);
        CordovaPageActivity waiting4Close = current.getActivity();
        startActivity(anim, intent);
        if(closeSelf){
            waiting4Close.finishInternal();
        }
    }

    private static void startActivity(String anim, final Intent intent) {
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
    }

    /**
     * 跳转接口
     * @param moduleId 需要显示的页面模块ID
     * @param url 需要显示的页面url
     * @param anim 页面切换动画效果
     * @param closeSelf 是否关闭自己
     * @param hideStatusBar 是否隐藏原生标题栏
     *
     */
    public static void deepLinkRedirect(String moduleId,String url, final int callbackId, String anim, boolean closeSelf,boolean hideStatusBar, CallbackContext callbackContext){
        if(current == null) return;
        final Intent intent = new Intent(current.getActivity(),CordovaPageActivity.class);
        Log.d("lqs1","id="+callbackId+"callbakc="+callbackContext);
        intent.putExtra("path",url);
        intent.putExtra("hide",hideStatusBar);
        intent.putExtra("moduleId",moduleId);
        intent.putExtra("deepLinkRedirect",true);
        current.setCallBack(callbackId,callbackContext);
        CordovaPageActivity waiting4Close = current.getActivity();
        startActivityForResult(callbackId, anim, intent);
        if(closeSelf){
            waiting4Close.finishInternal();
        }
    }

        /**
         * 跳转接口
         * @param url 需要显示的页面url
         * @param anim 页面切换动画效果
         * @param closeSelf 是否关闭自己
         * @param hideStatusBar 是否隐藏原生标题栏
         *
         */
    public static void redirect(String url, final int callbackId, String anim, boolean closeSelf,boolean hideStatusBar, CallbackContext callbackContext){
        if(current == null) return;
        final Intent intent = new Intent(current.getActivity(),CordovaPageActivity.class);
        Log.d("lqs1","id="+callbackId+"callbakc="+callbackContext);
        intent.putExtra("path",url);
        intent.putExtra("hide",hideStatusBar);
        current.setCallBack(callbackId,callbackContext);
        CordovaPageActivity waiting4Close = current.getActivity();
        startActivityForResult(callbackId, anim, intent);
        if(closeSelf){
            waiting4Close.finishInternal();
        }
    }

    private static void startActivityForResult(final int callbackId, String anim, final Intent intent) {
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
    }

    /**
     * 从指定activity栈顶之间的所有activity全部finish（也可以理解为返回到指定acitivity）
     * 比如A->B->C->D,如果想要把C,D全部finish掉，可以调用popPage(false,B)或者popPage(true,C)
     * @param popSelf 是否关闭自己
     * @param activity 指定开始finish的activity
     */
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

    /**
     * 关闭所有acitivity
     */
    public static void popAllPage(){
        while (current != null){
            goBack(null);
        }
    }

    /**
     * 返回上一个acitivty
     * @param data 传递的数据
     */
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
