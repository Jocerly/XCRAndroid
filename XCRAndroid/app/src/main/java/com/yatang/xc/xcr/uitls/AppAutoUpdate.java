package com.yatang.xc.xcr.uitls;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;

import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;

import org.jocerly.jcannotation.ui.ViewInject;
import org.jocerly.jcannotation.utils.FileUploadOrDownLoad;
import org.jocerly.jcannotation.utils.SDCardUtils;
import org.jocerly.jcannotation.utils.SystemTool;

import java.io.File;
import java.io.InterruptedIOException;
import java.util.HashMap;

/**
 * 自动检测更新下载类
 *
 * @author Jocerly
 */
public class AppAutoUpdate {
    private static final int FAIL = 0x00;
    private static final int SUCCESS = 0x01;
    private static final int LASTVERSION = 0x02;

    private Activity context;

    private String version;//服务器端的接口
    private String desc;//升级提示
    private String apkUrl;//apk地址
    private String isMandatory;//是否强制更新

    private ProgressDialog pd;
    private OnAppUpdateClickLister onAppUpdateClickLister;
    private HttpRequestService httpRequestService;
    private HashMap<String, Object> params;

    public AppAutoUpdate(Activity context) {
        this.context = context;
        params = new HashMap<String, Object>();
        httpRequestService = MyApplication.instance.getHttpRequestService();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    showUpdataDialog("1".equals(msg.obj.toString()));
                    break;
                case FAIL:
                    showFailure((boolean) msg.obj);
                    break;
                case LASTVERSION:
                    if ((boolean) msg.obj) {
                        ViewInject.toast(context, "已是最新版");
                    }
                    if (onAppUpdateClickLister != null) {
                        onAppUpdateClickLister.OnCancleClickLister();
                    }
                    break;
            }
        }
    };

    /**
     * 提示有新版本
     */
    protected void showUpdataDialog(final boolean isMandatory) {
        AlertDialog.Builder builer = new AlertDialog.Builder(context);
        builer.setTitle("有新版本");
        builer.setMessage(desc);
        builer.setPositiveButton(R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                downLoadApk(isMandatory);
            }
        });
        if (!isMandatory) {
            builer.setNegativeButton(R.string.cancle, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (onAppUpdateClickLister != null) {
                        onAppUpdateClickLister.OnCancleClickLister();
                    }
                }
            });
        }

        AlertDialog dialog = builer.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 下载apk
     */
    protected void downLoadApk(boolean isMandatory) {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    File file = FileUploadOrDownLoad.doDownLoadFile(SDCardUtils.DIR_PATH, Constants.PackageName, apkUrl, pd);
                    sleep(1000);
                    installApk(file);
                    pd.dismiss();
                } catch (InterruptedIOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    pd.dismiss();
                    Message msg = new Message();
                    msg.what = FAIL;
                    msg.obj = true;
                    handler.sendMessage(msg);
                }
            }
        };
        pd = new ProgressDialog(context) {
            @Override
            public void cancel() {
                super.cancel();
                thread.interrupt();
                ViewInject.toast(context, "取消下载");
                if (onAppUpdateClickLister != null) {
                    onAppUpdateClickLister.OnCancleClickLister();
                }
            }
        };
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("正在下载...");
        pd.show();
        if(isMandatory) {
            pd.setCancelable(false);
        }
        thread.start();
    }

    /**
     * 显示下载失败
     */
    public void showFailure(boolean isShown) {
        if (isShown) {
            ViewInject.toast(context, "更新失败");
        }
        if (onAppUpdateClickLister != null) {
            onAppUpdateClickLister.OnCancleClickLister();
        }
    }

    /**
     * 安装apk
     */
    protected void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, Constants.fileProvider, file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    /**
     * 版本信息校验
     */
    public void checkVersion(final boolean isShown) {
        try {
            params.clear();
            params.put("CurrentVersion", SystemTool.getAppVersionName(context));
            params.put("CurrentCode", SystemTool.getAppVersionCode(context));
            httpRequestService.doRequestData(context, "System/AppUpdate", false, params, new HttpRequestService.IHttpRequestCallback() {
                @Override
                public void onRequestCallBack(ResultParam resultParam) {
                    if (Constants.M00.equals(resultParam.resultId)) {
                        version = resultParam.mapData.get("Version");
                        desc = resultParam.mapData.get("Desc");
                        apkUrl = resultParam.mapData.get("ApkUrl");
                        isMandatory = resultParam.mapData.get("IsMandatory");

                        Message msg = new Message();
                        msg.what = SUCCESS;
                        msg.obj = isMandatory;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = LASTVERSION;
                        msg.obj = isShown;
                        handler.sendMessage(msg);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            Message msg = new Message();
            msg.what = FAIL;
            msg.obj = isShown;
            handler.sendMessage(msg);
        }
    }

    /**
     * 更新回调
     *
     * @author asus
     */
    public interface OnAppUpdateClickLister {
        public void OnCancleClickLister();
    }

    public void setOnAppUpdateClickLister(OnAppUpdateClickLister onAppUpdateClickLister) {
        this.onAppUpdateClickLister = onAppUpdateClickLister;
    }
}