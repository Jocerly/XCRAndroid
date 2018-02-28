package com.yatang.xc.xcr.uitls;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.yatang.plugin.navigation.CordovaPageActivity;
import com.yatang.plugin.navigation.module.ConfigPageEntity;
import com.yatang.plugin.navigation.module.ModuleEntity;
import com.yatang.xc.supchain.ZIPUtil;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.config.Config;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.plugin.navigation.module.Module;
import com.yatang.plugin.navigation.module.Versions;

import org.jocerly.jcannotation.utils.FileUploadOrDownLoad;
import org.jocerly.jcannotation.utils.SDCardUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.util.EncodingUtils;

import static com.yatang.plugin.navigation.module.UpdateStatus.UPDATE_STATUS_DOWNLOAD_FAIL;
import static com.yatang.plugin.navigation.module.UpdateStatus.UPDATE_STATUS_IDLE;
import static com.yatang.plugin.navigation.module.UpdateStatus.UPDATE_STATUS_LOAD_FAIL;
import static com.yatang.plugin.navigation.module.UpdateStatus.UPDATE_STATUS_UNZIP_FAIL;
import static com.yatang.xc.xcr.uitls.ModuleUpdaterManager.UPDATE_CORDOVA_PAGE_CONFIG;
import static com.yatang.xc.xcr.uitls.ModuleUpdaterManager.UPDATE_CORDOVA_VERSION;
import static com.yatang.xc.xcr.uitls.ModuleUpdaterManager.VERSIONS;

/**
 * Created by liaoqinsen on 2017/8/17 0017.
 */

public class SUPModuleUpdater implements Runnable{

    public static final String INDEX_HTML = "index.html";
    private int status = UPDATE_STATUS_IDLE;
    private static Handler handler;

    public SUPModuleUpdater(Module defualtModule, Module currentModule, Handler handler,boolean isReplace) {
        this.defualtModule = defualtModule;
        this.currentModule = currentModule;
        this.isReplace = isReplace;
        this.handler   = handler;
    }

    public SUPModuleUpdater(Module defualtVersion, Module currentVersion,Handler handler) {
        this(defualtVersion,currentVersion, handler,false);
    }

    public Module defualtModule = new Module();
    public Module currentModule = new Module();

    private Versions defualtVersion = new Versions("0","0");
    private Versions currentVersion = new Versions("0","0");

    //是否覆盖已存在的web包
    private boolean isReplace;
    private SUPModuleUpdater() {
    }

    /**
     * 获取当前使用中的web包的模块id和版本号
     */
    private boolean initVersionInfo() {
        Log.d("sup_updater","------------获取当前使用中的web包的模块id和版本号----------------");
        defualtVersion = defualtModule.getVersion();
        currentVersion = currentModule.getVersion();
        Log.d("sup_updater","defaultversion:"+defualtVersion.moduleId+"/"+defualtVersion.version);
        Log.d("sup_updater","currentversion:"+currentVersion.moduleId+"/"+currentVersion.version);

        if(currentVersion.isInvalid()){
            if(defualtVersion.isInvalid()){
                //assets包中找不到默认压缩包，暂时不考虑这种情况
                Log.d("sup_updater","assets包中找不到默认压缩包");
                status = UPDATE_STATUS_LOAD_FAIL;
                return true;
            }else{
                currentVersion.copy(defualtVersion);
                currentModule.copy(defualtModule);
            }
        }
        return false;
    }

    /**
     * 检查服务器版本，确定是否需要更新
     * @param moduleId 模块id
     * @param version 版本号
     * @return
     */
    private String checkServerVersionInfo(String moduleId, String version) {
        try {
            byte[] bytes = new byte[1024];
            int numReadByte = 0;
            StringBuffer sb = new StringBuffer();
            String path = Config.NetConfig.request_url + "System/AppModuleUpdate";

            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(Constants.MAXTIMEOUT);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Cookie", "DeviceId=" + MyApplication.instance.macAddress + ",Type=1");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ModuleId", moduleId);
            jsonObject.put("CurrentVersion", version);


            String params = "msg=" + URLEncoder.encode(jsonObject.toString(), "UTF-8");
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            out.writeBytes(params);
            out.flush();
            out.close();
            int status = connection.getResponseCode();
            InputStream in;
            if (status >= HttpStatus.SC_BAD_REQUEST)
                in = connection.getErrorStream();
            else
                in = connection.getInputStream();
            while ((numReadByte = in.read(bytes, 0, 1024)) > 0) {
                sb.append(new String(bytes, 0, numReadByte, "utf-8"));
            }
            in.close();
            ResultParam resultParam = DataAnalyze.doAnalyze(sb.toString());
            if ("00".equals(resultParam.resultId)) {
                String downloadUrl = null; //兼容服务端错误
                if (resultParam.mapData.get("ModuleId").equals(defualtModule.getModuleId())) {
                    currentVersion.version = resultParam.mapData.get("Version");
                    currentVersion.moduleId = resultParam.mapData.get("ModuleId");
                    currentModule.setVersion(currentVersion);
                    downloadUrl = resultParam.mapData.get("ZipUrl");
                }
                if (currentVersion.isEmpty() || downloadUrl == null || "null".equals(downloadUrl)) {
                    return null;
                }
                return downloadUrl;
            } else if ("01".equals(resultParam.resultId)) {
//                currentVersion.version = version;
//                currentVersion.moduleId = moduleId;
//                currentModule.setVersion(currentVersion);
                return "noUpdate";
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查zip包是否已经下载
     * @param moduleId
     * @param version
     * @return 1 表示需要解压zip包 -1表示需要重新下载zip包
     */
    private int checkNeedUnzip(String moduleId, String version) {
        if (moduleId == null || version == null) return -1;
        String webpackPath = CordovaPageActivity.getWebpackPath(MyApplication.instance, moduleId, version) + INDEX_HTML;
        File file = new File(webpackPath );
        if (file.exists()) return 0;
        else {
            String zipPath = CordovaPageActivity.getZipPath(MyApplication.instance, moduleId, version, true);
            File zipFile = new File(zipPath);
            return zipFile.exists() ? 1 : -1;
        }
    }

    /**
     * 完成更新，并将结果通知到监听器
     */
    private void tryComplete() {

        if (status > 0x3000) {
        } else {
            status = UPDATE_STATUS_IDLE;
        }
        currentModule.setVersion(currentVersion);
        Message message = Message.obtain();
        message.what = status;
        message.obj = currentModule;
        handler.sendMessage(message);
    }


    /**
     * 将assets中的默认zip包拷贝到缓存
     * @param zipName zip包的名字
     * @return
     */
    public static boolean copyZIPPack2Cache(String zipName ,Versions defualtVersion) {

        try {
            int bytesum = 0;
            int byteread = 0;
            InputStream inStream = null;
            inStream = MyApplication.instance.getAssets().open(zipName);
            File file = new File(CordovaPageActivity.getZipPath(MyApplication.instance, defualtVersion.moduleId, defualtVersion.version, true));
            if(!file.exists()){
                SDCardUtils.createFile(file.getParent(),(defualtVersion.moduleId + "_" + defualtVersion.version + ".zip"));
            }
            FileOutputStream fs = new FileOutputStream(CordovaPageActivity.getZipPath(MyApplication.instance, defualtVersion.moduleId, defualtVersion.version, true));
            byte[] buffer = new byte[1024];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解压zip包
     * @param file
     */
    private boolean unzipPackage(File file,String moduleId,String version) {
        Log.d("sup_updater","-----------------unzipPackage------------------------");
        try {
            long size = SDCardUtils.unZip(file, new File(CordovaPageActivity.getWebpackPath(MyApplication.instance, moduleId, version)));
            ZIPUtil.UnZipFolder(MyApplication.instance.getAssets().open("plugin.zip"), CordovaPageActivity.getWebpackPath(MyApplication.instance, moduleId, version));
            if (size <= 0) {
                Log.d("sup_updater","-----------------UPDATE_STATUS_UNZIP_FAIL:size<0------------------------");
                status = UPDATE_STATUS_UNZIP_FAIL;
                clearVersionAndFile(file, moduleId);
                return  false;
            }
            status = UPDATE_STATUS_IDLE;
            //解压是更新的最后一步，因此当前版本号应该更新为解压压缩包的版本号
            currentVersion.version = version;
            currentVersion.moduleId = moduleId;
            currentModule.setVersion(currentVersion);

            return true;
        } catch (Exception e) {
            Log.d("sup_updater","-----------------UPDATE_STATUS_UNZIP_FAIL:exception------------------------\n"+e.getMessage());
            e.printStackTrace();
            status = UPDATE_STATUS_UNZIP_FAIL;
            clearVersionAndFile(file, moduleId);
        }
        return false;
    }

    private void clearVersionAndFile(File file, String moduleId) {
        try {
            //如果解压文件失败，说明本地保存的版本信息对应的zip文件不可用，需要清空本地版本信息，使版本信息回到初始状态
            VersionInfoHelper.clearVersionInfo(moduleId);
            //如果zip文件存在但是无法解压，则删除该损坏的zip文件
            if(file.exists()) {
                file.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 下载zip包
     * @param url
     * @return
     */
    private File downloadPackge(String url,String moduleId,String version) {
        Log.d("sup_updater","-----------------downloadPackge------------------------");
        if (url == null) return null;
        try {
            File file = FileUploadOrDownLoad.doDownLoadFile(CordovaPageActivity.getZipPath(MyApplication.instance, moduleId, version, false), moduleId + "_" + version + ".zip", url, null);
            if (file != null) {
                VersionInfoHelper.addVersion(moduleId, version);
            }else{
                status = UPDATE_STATUS_DOWNLOAD_FAIL;
            }
            return file;
        } catch (InterruptedException e) {
            e.printStackTrace();
            status = UPDATE_STATUS_DOWNLOAD_FAIL;
        } catch (Exception e) {
            status = UPDATE_STATUS_DOWNLOAD_FAIL;
        }
        return null;
    }

    /**
     * 尝试加载上一个版本的，如果没有上个版本，再在assets中的默认zip包
     */
    private void tryLoadNearestModule() {
        Versions versions = VersionInfoHelper.getNearestModule(currentModule.getModuleId());
        if (versions != null && !TextUtils.isEmpty(versions.getModuleId()) && !TextUtils.isEmpty(versions.getVersion())) {
            currentVersion = versions;
            currentModule.setVersion(currentVersion);
            return;
        }
        unzipDefaultPackage();
        File file = new File(CordovaPageActivity.getZipPath(MyApplication.instance, currentVersion.moduleId, currentVersion.version, true));
        if (file.exists()) {
            unzipPackage(file, currentVersion.moduleId, currentVersion.version);
        } else {
            status = UPDATE_STATUS_LOAD_FAIL;
        }
    }

    private void unzipDefaultPackage(){
        copyZIPPack2Cache(defualtVersion.moduleId+"_"+defualtVersion.version+".zip",defualtVersion);
        currentVersion.copy(defualtVersion);
        currentModule.copy(defualtModule);
    }


    /**
     * 如果本地版本号为最新，但是缓存中没有zip包和解压后的web文件，并且本地默认的zip包跟当前版本号不匹配或者无法拷贝到缓存，则尝试强制更新到最新版本
     */
    private void tryUpdateNewestVersion(int reTryCount) {
        Log.d("sup_updater","-----------------tryUpdateNewestVersion------------------------");
        String u = checkServerVersionInfo(currentModule.getModuleId(), "0");
        downLoadZip(u,reTryCount);
    }

    private void downLoadZip(String url,int reTryCount){
        try {
            reTryCount ++;
            File zipFile = downloadPackge(url,currentVersion.moduleId,currentVersion.version);
            if (zipFile != null) {
                if(!unzipPackage(zipFile,currentVersion.moduleId,currentVersion.version)){
                    if(reTryCount < 3) {
                        tryUpdateNewestVersion(reTryCount);
                    }else{
                        tryLoadNearestModule();
                    }
                }
            }else{
                if(reTryCount < 3) {
                    tryUpdateNewestVersion(reTryCount);
                }else{
                    tryLoadNearestModule();
                }
            }
        } catch (Exception e) {
            status = UPDATE_STATUS_LOAD_FAIL;
        }
    }

    /**
     * 加载前端供应链工程中的Config.json文件，来读取首页广告跳转key与供应链页面的映射关系
     */
    public static void loadConfiPage(String moduleId, String version){
        String path = CordovaPageActivity.getWebpackPath(MyApplication.instance,moduleId,version)+"configPage.json";
        File file = new File(path);
        if(!file.exists()){
            return;
        }
        String config = "";
        try{
            config = FileUtils.readTxtFile(path);
            updateConfigPageEntity(moduleId,config);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateConfigPageEntity(String moduleId,String config) {
        Message message = Message.obtain();
        message.what = UPDATE_CORDOVA_PAGE_CONFIG;
        Bundle bundle = new Bundle();
        bundle.putString("moduleId",moduleId);
        bundle.putString("config",config);
        message.setData(bundle);
        handler.sendMessage(message);
    }


    @Override
    public void run() {
        try {
            Log.d("sup_updater", "------------开始更新供应链模块----------------");
            if (!initVersionInfo()) {  //初始化版本信息
                String zipUrl = checkServerVersionInfo(currentVersion.moduleId, currentVersion.version); //检查本地版本是否需要更新
                Log.d("sup_updater", "ZipUrl：" + (zipUrl == null ? "null" : zipUrl));
                if (zipUrl == null || "noUpdate".equals(zipUrl)) { //无需更新
                    int result = checkNeedUnzip(currentVersion.moduleId, currentVersion.version); //检查本地文件是否需要更新
                    Log.d("sup_updater", "neadUzip：" + result);
                    if (result < 0) {  //需要重新下载zip包
                        tryUpdateNewestVersion(0);
                    } else if (result == 1) { //zip包存在，可直接解压
                        File zipFile = new File(CordovaPageActivity.getZipPath(MyApplication.instance, currentVersion.moduleId, currentVersion.version, true));
                        if (!(zipFile.exists() && unzipPackage(zipFile, currentVersion.moduleId, currentVersion.version))) {
                            tryUpdateNewestVersion(0);
                        }
                    }
                } else {  //需要更新
                    downLoadZip(zipUrl, 0);
                }
            }
            //如果更新出错，则直接加载assets中存放的默认zip包
            if (status > 0x3000) {
                tryLoadNearestModule();
            }
            loadConfiPage(currentVersion.moduleId,currentVersion.version);
            VersionInfoHelper.clearVersionCache(currentModule.getModuleId());
            tryComplete();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
