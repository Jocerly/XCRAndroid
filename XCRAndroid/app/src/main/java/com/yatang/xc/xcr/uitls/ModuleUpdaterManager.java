package com.yatang.xc.xcr.uitls;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.yatang.plugin.navigation.CordovaPageActivity;
import com.yatang.plugin.navigation.module.ConfigPageEntity;
import com.yatang.plugin.navigation.module.Module;
import com.yatang.plugin.navigation.module.ModuleEntity;
import com.yatang.plugin.navigation.module.UpdateStatus;
import com.yatang.plugin.navigation.module.Versions;
import com.yatang.xc.supchain.ZIPUtil;
import com.yatang.xc.xcr.MyApplication;

import org.jocerly.jcannotation.utils.SDCardUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.util.EncodingUtils;

import static com.yatang.xc.xcr.uitls.SUPModuleUpdater.INDEX_HTML;

/**
 * Created by liuping on 2017/10/20.
 */

public class ModuleUpdaterManager {

    private ModuleUpdaterManager() {
    }

    private static class SingletonHolder {
        private static final ModuleUpdaterManager INSTANCE = new ModuleUpdaterManager();
    }

    public static ModuleUpdaterManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //模块
    private ModuleEntity defualModuleEntity = new ModuleEntity();
    private ModuleEntity currentModuleEntity = new ModuleEntity();

    public static final String VERSIONS = "cordova_versions_2";
    public static volatile boolean UPDATE_COMPLETE_STATUS;

    //任务
    private List<SUPModuleUpdater> supModuleUpdaterList = Collections.synchronizedList(new ArrayList<SUPModuleUpdater>());
    private List<String> moduleNameList = Collections.synchronizedList(new ArrayList<String>());

    //任务线程池
    private static ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(2);

    private static Handler handler;
    private static ModuleUpdaterManager.UpdateResultCallback callback;

    //更新的任务数量
    private volatile int completeTaskCount;
    private volatile int allTaskCount;
    public static int UPDATE_CORDOVA_VERSION = 500;
    public static int UPDATE_CORDOVA_PAGE_CONFIG= 600;

    /**
     * 检查更新是否完成
     *
     * @param callback
     */
    public static void checkUpdateCompleted(ModuleUpdaterManager.UpdateResultCallback callback) {
        ModuleUpdaterManager.callback = callback;
        if(callback != null ){
            CleanUpHelper.stopCleanTask();
        }
        if (getInstance() != null) {
            getInstance().tryComplete(UPDATE_COMPLETE_STATUS);
        }
    }


    /**
     * 完成更新，并将结果通知到监听器
     */
    private void tryComplete(final boolean status) {

        initHandler();
        if (!status) {
            if (callback != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(false);
                    }
                });
            }
        } else {
            if (callback != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(true);
                    }
                });
            }
        }
    }

    public void start() {
        start(false);
    }

    public void start(final boolean isReplace) {
        if (UPDATE_COMPLETE_STATUS && VersionInfoHelper.checkWebFileExists()) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                initHandler();
                //清理線程池所有任务
                clearTask();
                initVersionInfo();
                initTask(isReplace);
                allTaskCount = supModuleUpdaterList.size();
                //执行任务
                for (SUPModuleUpdater supModuleUpdater : supModuleUpdaterList) {

                    //初始化版本信息
                    initCordovaNewModuleEntity(supModuleUpdater.defualtModule);
                    //存儲有多少個運行模塊
                    threadPoolExecutor.execute(supModuleUpdater);
                }
            }
        }).start();

    }

    /**
     * 设置cordovapageActiviy 基本信息
     */
    private void initCordovaNewModuleEntity( Module defualtModule) {
        boolean initSuccess;
        //1.拿最近的版本号，
        initSuccess = initNearestCordovaVersion(defualtModule);
        if (!initSuccess) {
            //2.最近的版本号没有，assess默认包
            initAssetsCordovaVersion(defualtModule);
        }
    }

    private boolean initNearestCordovaVersion(Module defualtModule) {
        Versions versions = VersionInfoHelper.getNearestModule(defualtModule.getModuleId());
        if (versions != null && !TextUtils.isEmpty(versions.getModuleId()) &&
                !TextUtils.isEmpty(versions.getVersion())) {
            File file = new File(CordovaPageActivity.getWebpackPath(MyApplication.instance, versions.moduleId, versions.version)+ INDEX_HTML);
            if (file.exists()) {
                //更新当前版本的模块
                Module moduleEntity = new Module();
                moduleEntity.setVersion(versions);
                moduleEntity.setModuleId(versions.getModuleId());
                updateCordovaVersion(moduleEntity);
                SUPModuleUpdater.loadConfiPage(versions.moduleId,versions.version);
                return true;
            }
        }
        return false;
    }

    private void initAssetsCordovaVersion( Module defualtModule) {
       Versions defualtVersion = defualtModule.getVersion();

        File file = new File(CordovaPageActivity.getWebpackPath(MyApplication.instance, defualtVersion.moduleId, defualtVersion.version)+ INDEX_HTML);
        if (file.exists()) {
            //更新当前版本的模块
            updateCordovaVersion(defualtModule);
            return;
        }

        try {
            File zip = new File(CordovaPageActivity.getZipPath(MyApplication.instance, defualtVersion.moduleId, defualtVersion.version,true));
            if (!zip.exists()) {
                SUPModuleUpdater.copyZIPPack2Cache(defualtVersion.moduleId + "_" + defualtVersion.version + ".zip",defualtVersion);
            }

            if (zip.exists()) {
                long size = SDCardUtils.unZip(file, new File(CordovaPageActivity.getWebpackPath(MyApplication.instance, defualtVersion.moduleId, defualtVersion.version)));
                ZIPUtil.UnZipFolder(MyApplication.instance.getAssets().open("plugin.zip"), CordovaPageActivity.getWebpackPath(MyApplication.instance, defualtVersion.moduleId, defualtVersion.version));
                if (size > 0) {
                    //更新当前版本的模块
                    updateCordovaVersion(defualtModule);
                    SUPModuleUpdater.loadConfiPage(defualtVersion.moduleId ,defualtVersion.version);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCordovaVersion(Module module) {
        Message message = Message.obtain();
        message.what = UPDATE_CORDOVA_VERSION;
        message.obj = module;
        handler.sendMessage(message);
    }

    private void initTask(boolean isReplace) {
        Iterator iter = null;
        iter = defualModuleEntity.getModuleMap().entrySet().iterator();
        if (iter == null) {
            return;
        }
        //最开始没有信息
        while (iter.hasNext()) {
            Module currentModule;
            Module defualtModule;
            //获取某个Module
            Map.Entry entry = (Map.Entry) iter.next();
            defualtModule = (Module) entry.getValue();

            String defualtMoudleId = (String) entry.getKey();

            currentModule = currentModuleEntity.getModuleMap().get(defualtMoudleId);
            if (currentModule == null) {
                Versions currentVersion = new Versions(defualtMoudleId, "0");
                currentModule = new Module();
                currentModule.setModuleId(defualtMoudleId);
                currentModule.setVersion(currentVersion);
            }
            currentModuleEntity.updateModuleMap(currentModule.getModuleId(), currentModule);

            SUPModuleUpdater supModuleUpdater = new SUPModuleUpdater(defualtModule, currentModule, handler, isReplace);
            supModuleUpdaterList.add(supModuleUpdater);
            moduleNameList.add(defualtMoudleId);
        }
    }

    private void clearTask() {
        supModuleUpdaterList.clear();
        UPDATE_COMPLETE_STATUS = false;
        allTaskCount = 0;
        completeTaskCount = 0;
        //关闭线程
        threadPoolExecutor.shutdownNow();
        threadPoolExecutor = Executors.newFixedThreadPool(1);
        CleanUpHelper.stopCleanTask();
    }


    private void initHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    //todo 需要将当前模块的版本信息更新到cordoverpageactivity当中
                    if (msg.what == UpdateStatus.UPDATE_STATUS_IDLE || msg.what == UpdateStatus.UPDATE_STATUS_CHECK_VERSION_FAIL
                            || msg.what == UpdateStatus.UPDATE_STATUS_DOWNLOAD_FAIL || msg.what == UpdateStatus.UPDATE_STATUS_UNZIP_FAIL
                            || msg.what == UpdateStatus.UPDATE_STATUS_LOAD_FAIL) {

                        //完成一项任务
                        completeTaskCount++;
                        Module currentModuel = (Module) msg.obj;
                        //更新当前版本的模块
                        CordovaPageActivity.getNewModuleEntity().updateModuleMap(currentModuel.getModuleId(), currentModuel);
                        //通知外界我完成了所有任務,将错误的模块发送出啊去
                        if (completeTaskCount == allTaskCount) {
                            UPDATE_COMPLETE_STATUS = true;
                            tryComplete(true);
                            //todo 删除文件
                            CleanUpHelper.startCleanTask(moduleNameList);
                        } else {
                            UPDATE_COMPLETE_STATUS = false;
                        }
                    }else if(msg.what == UPDATE_CORDOVA_VERSION){
                        Module currentModuel = (Module) msg.obj;
                        CordovaPageActivity.getNewModuleEntity().updateModuleMap(currentModuel.getModuleId(), currentModuel);
                    }else if(msg.what == UPDATE_CORDOVA_PAGE_CONFIG) {
                        try {
                            Bundle bundle = msg.getData();
                            String moduleId = bundle.getString("moduleId");
                            String config = bundle.getString("config");
                            ConfigPageEntity configPageEntity = CordovaPageActivity.getNewConfigPageEntity();
                            ConfigPageEntity.setConfigPage(configPageEntity.getConfigPageMap(), moduleId, new JSONObject(config));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    super.handleMessage(msg);
                }
            };
        }
    }

    /**
     * 获取当前使用中的web包的模块id和版本号
     */
    private void initVersionInfo() {
        Log.d("sup_updater", "------------获取当前使用中的web包的模块id和版本号----------------");
        ModuleEntity defaultEntity = VersionInfoHelper.initDefualtVersionInfo();
        defualModuleEntity.setModuleMap(defaultEntity.getModuleMap());
        ModuleEntity currentEntity = VersionInfoHelper.initCurrentVersionInfo();
        currentModuleEntity.setModuleMap(currentEntity.getModuleMap());
    }

    public interface UpdateResultCallback {
        public void onResult(boolean isSuccess);

        public void onProgress(int current, int max);
    }






}
