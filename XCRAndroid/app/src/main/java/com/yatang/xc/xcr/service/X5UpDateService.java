package com.yatang.xc.xcr.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;
import com.yatang.xc.xcr.MyApplication;

import org.jocerly.jcannotation.utils.JCLoger;

/**
 * X5内核下载服务
 * Created by Jocerly on 2017/7/19.
 */

public class X5UpDateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initTbs();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initTbs() {
        final QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                JCLoger.debug("View是否初始化完成:" + arg0);
                MyApplication.instance.isX5Over = arg0;
                stopSelf();
            }

            @Override
            public void onCoreInitFinished() {
                JCLoger.debug("X5内核初始化完成");
            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                JCLoger.debug("腾讯X5内核 下载结束");
                QbSdk.initX5Environment(getApplicationContext(), cb);
            }

            @Override
            public void onInstallFinish(int i) {
                JCLoger.debug("腾讯X5内核 安装完成");
                MyApplication.instance.isX5Over = true;
                stopSelf();
            }

            @Override
            public void onDownloadProgress(int i) {
                JCLoger.debug("腾讯X5内核 下载进度:%" + i);
            }
        });

        TbsDownloader.needDownload(this, true, true, new TbsDownloader.TbsDownloaderCallback() {
            @Override
            public void onNeedDownloadFinish(boolean b, int i) {
                JCLoger.debug(b + "-----腾讯X5内核 下载进度:%" + i);
                MyApplication.instance.isX5Over = !b;
                stopSelf();
            }
        });
    }
}
