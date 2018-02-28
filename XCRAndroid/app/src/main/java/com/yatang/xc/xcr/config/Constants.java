package com.yatang.xc.xcr.config;

public class Constants {
    public final static int SUCCESS = 0x01;//成功的标识
    public final static int FAIL = 0x00;//失败的标识

    /**
     * 超时时间
     */
    public static final int MAXTIMEOUT = 30 * 1000;
    public static int RefreshTime = 1400;

    public static final String M00 = "00";//正常
    public static final String M01 = "01";
    public static final String M02 = "02";
    public static final String M03 = "03";
    public static final String M04 = "04";
    public static final String M05 = "05";//被挤下线
    public static final String M06 = "06";
    public static final String M13 = "13";
    public static final String M14 = "14";
    public static final String M15 = "15";
    public static final String M98 = "98";
    public static final String M100 = "100";
    public static final String M1000 = "1000";
    public static final String M600503 = "600503";

    public static final String SimpleDateFormat = "yyyy-MM-dd";
    public static final String SimpleTimeFormat = "HH:mm";

    public static final String timeStr = "yyyy-MM-dd HH:mm:ss";
    /**
     * 百度语音
     */
    public static final String appId = "10231913";
    public static final String appKey = "8r65Pbh6WQKHCzGGzHrDWpfs";
    public static final String secretKey = "7c64fee4fa9836a27227d5917b978225";

    /**
     * 页面一次性数据条数
     */
    public static final int PageSize = 20;

    public static final String PreferenceName = "XCRAndroid";
    public static final String PackageName = "XCRAndroid.apk";
    public static final String fileProvider = "com.camera_photos.fileProvider";
    public static final String CrashReportId = "6115a6bf8c";//出现异常bug代码提交统计Id

    public static final class Permission {
        public static final int CALL_PHONE = 1;
        public static final int CAMERA = 2;
        public static final int EXTERNAL_STORAGE = 3;
        public static final int READ_EXTERNAL_STORAGE = 4;
        public static final int ACCESS_FINE_LOCATION = 5;
        public static final int ACCESS_COARSE_LOCATION = 6;
        public static final int CAMERA_SCAN = 7;
        public static final int READ_PHONE_STATE = 8;
    }

    /**
     * Intent回调
     *
     * @author Jocerly
     */
    public final class ForResult {
        public static final int CHOICE_STORE = 1;
        public static final int SCAN = 2;//扫码查询
        public static final int ADD_GOODS = 3;
        public static final int CHOICE_GOODS = 4;
        public static final int EDIT_GOODS = 5;
        public static final int SCAN_CODE = 6;//扫码调价
        public static final int CHOICE_CLASSIFY = 7;
        public static final int STUDY_SUC = 8;
        public static final int FILECHOOSER_RESULTCODE = 9;
        public static final int SET_STORE_MSG = 10;
    }

    /**
     * SharePreference
     *
     * @author Jocerly
     */
    public final class Preference {
        public static final String Registration_Id = "Registration_Id";
        public static final String UserId = "UserId";
        public static final String UserName = "UserName";
        public static final String CityName = "CityName";
        public static final String RUserInfoKey = "RUserInfoKey";
        public static final String BranchOfficeId = "BranchOfficeId";
        public static final String UserNo = "UserNo";
        public static final String FinancialAccount = "FinancialAccount";
        public static final String StoreSerialNoDefault = "StoreSerialNoDefault";
        public static final String StoreSerialNameDefault = "StoreSerialNameDefault";
        public static final String StoreAbbreName = "StoreAbbreName";
        public static final String StoreSerialPicDefault = "StoreSerialPicDefault";
        public static final String Token = "Token";
        public static final String MsgNum = "MsgNum";
        public static final String LoginId = "LoginId";
        public static final String ClassifyDate = "ClassifyDate";
        public static final String MaxClassTime = "MaxClassTime";
        public static final String UserPhone = "UserPhone";
        public static final String ADPicUrl = "ADPicUrl";
        public static final String AdJumpUrl = "AdJumpUrl";
        public static final String VipIdentify = "VipIdentify";
        public static final String StoreNo = "StoreNo";
    }

    /**
     * 子项目编号
     */
    public final class Subproject {
        /**
         * 供应链子项目
         */
        public static final String SupChain = "001";
    }
}
