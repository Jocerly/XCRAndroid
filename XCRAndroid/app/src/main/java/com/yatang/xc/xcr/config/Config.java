package com.yatang.xc.xcr.config;

import com.yatang.xc.xcr.BuildConfig;

import java.security.PublicKey;

/**
 * 项目配置类
 *
 * @author Jocerly
 */
public class Config {

    /**
     * 是否在调试阶段
     */
    public final static boolean isDebug = BuildConfig.DEBUG;

    /**
     * 网络配置
     *
     * @author Jocerly
     */
    public static class NetConfig {
        /**
         * api调用地址
         */
        public static String request_url = BuildConfig.CLIENT_HOST;
//        public final static String request_url = "http://172.30.10.214:8088/xcr-web-app/";//UAT
//        public final static String request_url = "http://172.30.40.214:8080/xcr-web-app/";//高大伟
//		public final static String request_url = "https://xcrapp.yatang.com.cn/";//线上环境

        /**
         * 激光推送标签定义
         */
        public static String OnTag = BuildConfig.OnTag;
    }


    /*** --- 接口API ----*/
    public final class NetSitConfig {
        public final static String request_url = "http://172.30.11.20:30188/xcr-web-app/";//sit
        public static final String OnTag = "XCR_Test";
    }

    public final class NetUatConfig {
        public final static String request_url = "http://172.30.10.214:8088/xcr-web-app/";//UAT
        public static final String OnTag = "XCR_Test";
    }

    public final class NetProConfig {
        public final static String request_url = "https://xcrapptest.yatang.com.cn/";//测试线上环境
        public static final String OnTag = "XCR_Online";
    }

    /**
     * 服务器公钥
     */
    public static PublicKey serverKey;
    /**
     * 客户端私钥
     */
    public static String customerKey;
}
