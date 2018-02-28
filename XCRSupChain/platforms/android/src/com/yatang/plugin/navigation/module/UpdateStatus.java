package com.yatang.plugin.navigation.module;

import android.util.SparseArray;

/**
 * Created by liuping on 2017/10/23.
 */

public class UpdateStatus {

    public static final SparseArray<String> ERROR_MSG = new SparseArray<>();

    public static final int UPDATE_STATUS_IDLE = 0X2000;
    public static final int UPDATE_STATUS_CHECK_VERSION_FAIL = 0X3001;
    public static final int UPDATE_STATUS_DOWNLOAD_FAIL = 0X3002;
    public static final int UPDATE_STATUS_UNZIP_FAIL = 0X3003;
    public static final int UPDATE_STATUS_LOAD_FAIL= 0X3004;

    static {
        ERROR_MSG.put(UPDATE_STATUS_CHECK_VERSION_FAIL, "获取版本信息出错");
        ERROR_MSG.put(UPDATE_STATUS_DOWNLOAD_FAIL, "下载模块压缩包出错");
        ERROR_MSG.put(UPDATE_STATUS_UNZIP_FAIL, "解压模块压缩包出错");
        ERROR_MSG.put(UPDATE_STATUS_LOAD_FAIL, "加载供应链模块出错，请尝试重新安装小超商家版APP");
    }
}
