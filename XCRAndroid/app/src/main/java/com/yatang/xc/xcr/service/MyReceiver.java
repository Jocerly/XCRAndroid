package com.yatang.xc.xcr.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.CollectTransactionListActivity;
import com.yatang.xc.xcr.activity.MainActivity;
import com.yatang.xc.xcr.activity.MsgActivity;
import com.yatang.xc.xcr.activity.OrderDetailsActivity;
import com.yatang.xc.xcr.activity.OrderManagementActivity;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.tts.SpeechUtil;

import org.jocerly.jcannotation.ui.JCActivityStack;
import org.jocerly.jcannotation.utils.JCLoger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;


/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
@SuppressLint("NewApi")
public class MyReceiver extends BroadcastReceiver {
    private Vibrator vibrator;  //震动
    private MediaPlayer player;
    private SpeechUtil speechUtil;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {//接收RegistrationId
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            JCLoger.debug("[MyReceiver] 接收Registration Id : " + regId);
            Common.setAppInfo(context, Constants.Preference.Registration_Id, regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {//自定义消息
            setReceiveMsg(context, bundle.getString(JPushInterface.EXTRA_MESSAGE));
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {//通知
            setReceiveNotice(context, bundle.getString(JPushInterface.EXTRA_EXTRA));
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {//击打开了通知
            openReceiveNotice(context, bundle.getString(JPushInterface.EXTRA_EXTRA));
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            JCLoger.debug("[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            JCLoger.debug("[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            if (connected && !JPushInterface.getConnectionState(context)) {
                JPushInterface.onResume(context);
            }
        } else {
            JCLoger.debug("[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    /**
     * 处理打开通知
     * @param context
     * @param msg
     */
    private void openReceiveNotice(Context context, String msg) {
        JCLoger.debug("[MyReceiver] 用户点击打开了通知: " + msg);
        try {
            Activity activity = JCActivityStack.create().topActivity();
            JSONObject jsonObject = new JSONObject(msg);
            //0：消息，1：订单，2：蜂鸟配送接单提醒，3：蜂鸟配送拒单提醒，4：退货单，5：（用户取消订单、超时未接单、蜂鸟配送完成）、6：收钱码交易、7：待接单提示
            String type = jsonObject.getString("Type");
            Intent intent = null;
            if ("0".equals(type)) {
                if (activity != null && activity instanceof MsgActivity) {//直接刷新界面
                    ((MsgActivity) activity).refrashData();
                } else {//其他界面直接打开
                    intent = new Intent(context.getApplicationContext(), MsgActivity.class);//将要跳转的界面
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            } else if ("6".equals(type)) {//6：收钱码交易
                intent = new Intent(context.getApplicationContext(), CollectTransactionListActivity.class);//将要跳转的界面
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Bundle extras = new Bundle();
                if ("4".equals(type)) {
                    extras.putString("CancelId", jsonObject.getString("CancelId"));
                } else {
                    extras.putString("OrderNo", jsonObject.getString("OrderNo"));
                }
                //其他界面直接打开
                intent = new Intent(context.getApplicationContext(), OrderDetailsActivity.class);//将要跳转的界面
                intent.putExtras(extras);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理通知
     *
     * @param context
     * @param msg
     */
    private void setReceiveNotice(Context context, String msg) {
        JCLoger.debug("[MyReceiver] 接收到推送下来的通知: " + msg);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2 * 1000);

        try {
            Activity activity = JCActivityStack.create().topActivity();
            JSONObject jsonObject = new JSONObject(msg);
            //0：消息，1：订单，2：蜂鸟配送接单提醒，3：蜂鸟配送拒单提醒，4：退货单，5：（用户取消订单、超时未接单、蜂鸟配送完成）、6：收钱码交易、7：待接单提示
            String type = jsonObject.getString("Type");
            if ("0".equals(type)) {
                String num = Common.getAppInfo(context, Constants.Preference.MsgNum, "0");
                Common.setAppInfo(context, Constants.Preference.MsgNum, (Integer.parseInt(num) + 1) + "");
                if (activity != null) {
                    if (activity instanceof MainActivity) {//直接刷新界面
                        ((MainActivity) activity).refrashMsgNum();
                    }
                    if (activity instanceof MsgActivity) {//直接刷新界面
                        ((MsgActivity) activity).refrashData();
                    }
                }
            } else {
                int fileid = 0;
                if ("1".equals(type)) {
                    fileid = R.raw.order_new;
                } else if ("2".equals(type)) {
                    fileid = R.raw.order_fengliao;
                } else if ("3".equals(type)) {
                    fileid = R.raw.order_fl_refused;
                } else if ("6".equals(type)) {
                    if (speechUtil == null) {
                        speechUtil = new SpeechUtil(context);
                    }
                    try {
                        speechUtil.speak(jsonObject.getString("TransContent"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if ("7".equals(type)) {
                    fileid = R.raw.no_handle_order;
                }
                if (fileid != 0) {
                    try {
                        player = new MediaPlayer();
                        AssetFileDescriptor file = context.getResources().openRawResourceFd(fileid);
                        player.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                        player.prepare();
                        player.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (activity != null && activity instanceof OrderManagementActivity) {//直接刷新界面
                    ((OrderManagementActivity) activity).refreshWaitList();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理自定义消息
     *
     * @param context
     * @param msg
     */
    private void setReceiveMsg(Context context, String msg) {
        JCLoger.debug("[MyReceiver] 接收到推送下来的自定义消息: " + msg);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(msg);
            try {
                player = new MediaPlayer();
                AssetFileDescriptor file = context.getResources().openRawResourceFd(R.raw.order_new);
                player.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                player.prepare();
                player.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(2 * 1000);
            processCustomMessage(context, jsonObject.getString("ShortMsg"));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 打印所有的 intent extra 数据
     */
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    JCLoger.debug("This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    /**
     * send msg to Activity
     *
     * @param context
     * @param shortMsg
     */
    private void processCustomMessage(Context context, String shortMsg) {
        try {
            Activity activity = JCActivityStack.create().topActivity();
            if (activity != null && activity instanceof MsgActivity) {//直接刷新界面
                ((MsgActivity) activity).refrashData();
            } else {//其他界面直接通知
                showNotifictionIcon(context, shortMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示自定义消息通知
     *
     * @param context
     * @param msg
     */
    private void showNotifictionIcon(Context context, String msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Intent intent = new Intent(context, MsgActivity.class);//将要跳转的界面
        builder.setAutoCancel(true);//点击后消失
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        builder.setContentTitle(context.getResources().getString(R.string.app_name));
        builder.setContentText(msg);//通知内容
        //利用PendingIntent来包装我们的intent对象,使其延迟跳转
        PendingIntent intentPend = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(intentPend);
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

}
