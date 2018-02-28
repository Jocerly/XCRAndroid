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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created liuping on 29.12.15.
 * <p/>
 * Helper class to clean up file system and remove old releases folders.
 */
public class CleanUpHelper {
    private final File rootFolder;
    public static ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(1);

    public  static void startCleanTask(List<String> moduleList) {

        if(threadPoolExecutor == null){
            threadPoolExecutor = Executors.newFixedThreadPool(1);
        }

        for (String moduleId : moduleList) {

            String version = VersionInfoHelper.getAppVersion();

            ModuleEntity entity = ModuleEntity.getfromJson(MyApplication.instance.getBaseContext(), version);
            if (entity == null) {
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
            for (int i = mvs.length - 1; i >= 0; i--) {
                String[] mvInfo = mvs[i].split("_");
                if (mvInfo.length > 1) {
                    if (newVersionInfo.split(",").length <= 1) {
                        if (versionCount == 2) {
                            break;
                        }
                        newVersionInfo = mvs[i] + "," + newVersionInfo;
                        versionCount++;
                        versionList.add(mvInfo[1]);
                    }
                }
            }

            //清理其他版本的文件
            removeAbandonFolders(MyApplication.instance, moduleId, versionList);
        }
    }


    /**
     * Constructor.
     *
     * @param rootFolder root folder, where releases are stored
     */
    private CleanUpHelper(final String rootFolder) {
        this.rootFolder = new File(rootFolder);
    }

    /**
     * Remove release folders.
     *
     * @param context          application context
     * @param excludedVersionIds which releases are leave alive.
     */
    public  static void removeAbandonFolders(final Context context, final String moduleName, final List<String> excludedVersionIds) {
        try {
            final String rootFolder = CordovaPageActivity.getWebpackRootPath(context, moduleName);
            final String zippFolder = CordovaPageActivity.getZipPath(context, moduleName, null, false);

            if (excludedVersionIds == null || excludedVersionIds.size() <= 0) {
                return;
            }
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    new CleanUpHelper(rootFolder).removeFolders(excludedVersionIds);
                    new CleanUpHelper(zippFolder).removeZips(moduleName, excludedVersionIds);

                }
            });
        } catch (Exception e) {
           e.printStackTrace();
        }

    }



    private synchronized void removeFolders(final List<String> excludedReleases) {
        if (!rootFolder.exists() || excludedReleases == null || excludedReleases.size() <=0) {
            return;
        }

        File[] files = rootFolder.listFiles();
        for (File file : files) {
            boolean isIgnored = false;
            for (String excludedReleaseName : excludedReleases) {
                if (TextUtils.isEmpty(excludedReleaseName)) {
                    continue;
                }

                if (file.getName().equals(excludedReleaseName) || file.getName().equals("1") ) {
                    isIgnored = true;
                    break;
                }
            }

            if (!isIgnored) {
                Log.d("CHCP", "Deleting old release folder: " + file.getName());
                FileUtils.safeDelete(file);
            }
        }
    }



    private synchronized void removeZips(final String moduleName,final List<String> includedReleases) {
        if (!rootFolder.exists() || includedReleases == null || includedReleases.size() <=0) {
            return;
        }

        File[] files = rootFolder.listFiles();
        for (File file : files) {
            boolean isIgnored = false;
            for (String excludedReleaseName : includedReleases) {
                if (TextUtils.isEmpty(excludedReleaseName)) {
                    continue;
                }

                if (file.getName().equals(moduleName + "_" + excludedReleaseName +".zip") || file.getName().equals(moduleName+"_1.zip")) {
                    isIgnored = true;
                    break;
                }
            }

            if (!isIgnored && file.getName().startsWith(moduleName)) {
                Log.d("CHCP", "Deleting old release folder: " + file.getName());
                FileUtils.safeDelete(file);
            }
        }
    }

    public static void stopCleanTask(){

        if(threadPoolExecutor != null){
            threadPoolExecutor.shutdownNow();
            threadPoolExecutor = null;
        }
    }


}
