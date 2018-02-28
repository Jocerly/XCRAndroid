package com.yatang.xc.xcr.uitls;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.yatang.plugin.navigation.CordovaPageActivity;
import com.yatang.plugin.navigation.module.Module;
import com.yatang.plugin.navigation.module.ModuleEntity;
import com.yatang.plugin.navigation.module.Versions;
import com.yatang.xc.xcr.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.yatang.xc.xcr.uitls.ModuleUpdaterManager.VERSIONS;
import static com.yatang.xc.xcr.uitls.SUPModuleUpdater.INDEX_HTML;

/**
 * Created by liuping on 2017/10/23.
 */

public class VersionInfoHelper {


    /**
     * 将最新的版本号存到share preference
     *
     * @param moduleId
     * @param version
     */
    public  static void addVersion(String moduleId, String version) {
        if (TextUtils.isEmpty(moduleId)|| TextUtils.isEmpty(version)) {
            return;
        }
        String versions = getAppVersion();
        //需要转化
        ModuleEntity entity = ModuleEntity.getfromJson(MyApplication.instance.getBaseContext(), versions);
        Module module = entity.getModuleMap().get(moduleId);
        if (module == null) {
            module = new Module();
            Versions versions1 = new Versions("", "");
            module.setVersion(versions1);
            module.setModuleId(moduleId);
        }
          versions = module.getVersion().getVersion();
        //将当前版本信息更改
        if (!versions.contains(moduleId + "_" + version)) {
            versions += ("," + moduleId + "_" + version);
        }

        Versions versionInfo = module.getVersion();
        versionInfo.setVersion(versions);
        versionInfo.setModuleId(moduleId);
        entity.updateModuleMap(moduleId, module);

        //保存到sharePrefrence
        String stringEntity = ModuleEntity.getJsonStringByEntity(MyApplication.instance, entity);
        setAppVersion(stringEntity);
    }

    public  static Versions getNearestModule(String moduleId) {
        Versions currentVersion = null;
        String version = getAppVersion();
        ModuleEntity entity = ModuleEntity.getfromJson(MyApplication.instance.getBaseContext(), version);

        if(entity == null){
            return currentVersion;
        }
        Module module = entity.getModuleMap().get(moduleId);
        if (module == null ||  module.getVersion() == null) {
            return currentVersion;
        }
        Versions versions = module.getVersion();
        String versionInfo = versions.getVersion();
        if(TextUtils.isEmpty(versionInfo)){
            return currentVersion;
        }
        String[] mvs = versionInfo.split(",");
        if(mvs == null || mvs.length == 0){
            return currentVersion;
        }
        for (int i = mvs.length - 1; i >= 0; i--) {
            String[] s = mvs[i].split("_");
            if (s.length > 1) {
                File file = new File(CordovaPageActivity.getWebpackPath(MyApplication.instance, s[0], s[1]));
                if (file.exists()) {
                    currentVersion = new Versions(s[0], s[1]);
                    return currentVersion;
                }
            }
        }
        return currentVersion;
    }

    public  static void clearVersionInfo(String moduleId) {
        String version =getAppVersion();
        ModuleEntity entity = ModuleEntity.getfromJson(MyApplication.instance.getBaseContext(), version);
        entity.removeModuleMap(moduleId);

        //保存到sharePrefrence
        String stringEntity = ModuleEntity.getJsonStringByEntity(MyApplication.instance, entity);
        setAppVersion(stringEntity);
    }


    public  static void clearVersionCache(String moduleId) {

        String version = getAppVersion();

        ModuleEntity entity = ModuleEntity.getfromJson(MyApplication.instance.getBaseContext(), version);
        if(entity == null){
            return;
        }
        Module module = entity.getModuleMap().get(moduleId);
        if (module == null) {
            return;
        }
        Versions versions = module.getVersion();
        String versionInfo = versions.getVersion();

        if (TextUtils.isEmpty(versionInfo)) {
            return;
        }
        String newVersionInfo = "";
        String[] mvs = versionInfo.split(",");
        if (mvs == null || mvs.length < 1) {
            return;
        }

        //只保留两个版本
        int versionCount = 0;
        List<String> versionList = new ArrayList<>();
        for (int i = mvs.length -1; i >= 0; i--) {
            String[] mvInfo = mvs[i].split("_");
            if (mvInfo.length > 1) {
                if (newVersionInfo.split(",").length <= 1) {
                    if(versionCount == 2){
                        break;
                    }
                    newVersionInfo = mvs[i] + "," + newVersionInfo;
                    versionCount ++;
                    versionList.add(mvInfo[1]);
                }
            }
        }
        versions.setVersion(newVersionInfo);
        module.setVersion(versions);
        entity.updateModuleMap(moduleId, module);

        //保存到sharePrefrence
        String StringEntity = ModuleEntity.getJsonStringByEntity(MyApplication.instance, entity);
        setAppVersion(StringEntity);
    }


    public synchronized static void setAppVersion(String version){
        Common.setAppInfo(MyApplication.instance, VERSIONS, version);
    }

    public synchronized static String getAppVersion(){
        return Common.getAppInfo(MyApplication.instance, VERSIONS, "");
    }


    /**
     * 初始化默认包版本
     */
    public static ModuleEntity initDefualtVersionInfo() {
        ModuleEntity moduleEntity = new ModuleEntity();
        try {
            String[] files = MyApplication.instance.getAssets().list("");
            for (String fname : files) {
                if (fname.endsWith(".zip") && fname.contains("_")) {

                    Module defualtModule = new Module();
                    Versions defualtVersion = new Versions("0", "0");
                    defualtModule.setVersion(defualtVersion);
                    defualtModule.setModuleId("0");

                    String[] mv = fname.split("\\.")[0].split("_");

                    if (mv != null && mv.length > 1) {
                        defualtVersion.setModuleId(mv[0]);
                        defualtVersion.setVersion(mv[1]);

                        defualtModule.setVersion(defualtVersion);
                        defualtModule.setModuleId(mv[0]);
                    }
                    moduleEntity.updateModuleMap(defualtModule.getModuleId(), defualtModule);
                }
            }
        } catch (Exception e) {
            Log.d("sup_updater", "获取asset包出错" + e.getMessage());
            e.printStackTrace();
        }

        return moduleEntity;
    }


    /**
     * mvs {从share preference中获取到的当前web包的模块版本信息}
     * 存储格式为   100_1,100_2,100_3,101_1
     * 逗号隔开的为模块和版本信息，越靠后版本号越新
     */
    public static ModuleEntity initCurrentVersionInfo() {
        ModuleEntity moduleEntity = new ModuleEntity();
        String versionInfo = getAppVersion();
        ModuleEntity entity = ModuleEntity.getfromJson(MyApplication.instance.getBaseContext(), versionInfo);

        if (entity != null && !entity.getModuleMap().isEmpty() && entity.getModuleMap().size() > 0) {
            Map<String, Module> moduleMap = entity.getModuleMap();
            Iterator iter = moduleMap.entrySet().iterator();
            if (iter == null) {
                return moduleEntity;
            }
            while (iter.hasNext()) {
                //获取某个Module
                Map.Entry entry = (Map.Entry) iter.next();
                Module module = (Module) entry.getValue();
                String moduleId = (String) entry.getKey();

                //拿到版本信息
                String versionIds = module.getVersion().getVersion();
                if (TextUtils.isEmpty(versionIds)) {
                    return moduleEntity;
                }
                String[] mvs = versionIds.split(",");
                //拿到所以的版本信息
                Versions currentVersion = new Versions("0", "0");
                Module currentModuel = new Module();
                currentModuel.setVersion(currentVersion);
                currentModuel.setModuleId("0");

                if (mvs != null && mvs.length > 0) {
                    String mvInfo = mvs[mvs.length - 1];
                    String[] mv = mvInfo.split("_");
                    if (mv != null && mv.length > 1) {

                        currentVersion.setModuleId(mv[0]);
                        currentVersion.setVersion(mv[1]);

                        currentModuel.setVersion(currentVersion);
                        currentModuel.setModuleId(mv[0]);
                    }
                }
                moduleEntity.updateModuleMap(moduleId, currentModuel);
            }
        }
        return moduleEntity;
    }



    /**
     * 检查所有的模块当前文件
     * @return
     */
    public static boolean checkWebFileExists() {

        //TODO  判斷CordovaPageActivity 所有的 newmodule 文件是否存在，不存在更新
        try {
            ModuleEntity entity = CordovaPageActivity.getNewModuleEntity();
            if (entity != null && !entity.getModuleMap().isEmpty() && entity.getModuleMap().size() > 0) {
                Map<String, Module> moduleMap = entity.getModuleMap();
                Iterator iter = moduleMap.entrySet().iterator();
                if (iter == null) {
                    return false;
                }
                while (iter.hasNext()) {
                    //获取某个Module
                    Map.Entry entry = (Map.Entry) iter.next();
                    Module module = (Module) entry.getValue();
                    String moduleId = (String) entry.getKey();
                    File file = new File(CordovaPageActivity.getWebpackPath(MyApplication.instance, moduleId, module.getVersion().getVersion()) + INDEX_HTML);//
                    if(file.exists()){
                        continue;
                    }else {
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
