package com.yatang.xc.xcr.uitls;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.yatang.xc.xcr.config.Constants;

import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import static java.lang.Double.parseDouble;

public class Common {
    /**
     * 为指定edittext添加限制小数点
     *
     * @param editText
     * @param precision 小数点后允许的位数
     */
    public static void setDecimalFilter(EditText editText, final int precision) {
        if (editText != null) {
            InputFilter lengthFilter = new InputFilter() {

                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    if ("".equals(source)) {
                        return null;
                    } else {
                        String[] csArray = dest.toString().split("\\.");
                        if (csArray.length > 1 && csArray[1].length() >= precision) {
                            return "";
                        }

                        if (".".equals(source)) {
                            if (dest.length() - dstart > precision) {
                                return "";
                            }
                        }
                    }
                    return source;
                }
            };
            editText.setFilters(new InputFilter[]{lengthFilter});
        }
    }

    /**
     * 将时间戳转换为时间
     */
    public static String stampToDate(SimpleDateFormat simpleDateFormat, String s) {
        String res;
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s, String type) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(type);
        String res;
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 获取日期
     */
    public static String getDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date).toString();
    }

    /**
     * 判断是否为今日
     *
     * @param date
     * @return
     */
    public static boolean isToday(String date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        if (date.equals(fmt.format(new Date()).toString())) {//格式化为相同格式
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据指定的日期字符串获取星期几
     *
     * @param strDate 指定的日期字符串(yyyy-MM-dd 或 yyyy/MM/dd)
     * @return week
     */
    public static String getWeekBDayyDateStr(String strDate) {
        String week = "";
        String str[] = null;
        if (strDate.contains("-")) {
            str = strDate.split("-");
        } else if (strDate.contains("/")) {
            str = strDate.split("/");
        }

        int year = Integer.parseInt(str[0]);
        int month = Integer.parseInt(str[1]);
        int day = Integer.parseInt(str[2]);

        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day - 1);

        int weekIndex = c.get(Calendar.DAY_OF_WEEK);

        JCLoger.debug("------" + weekIndex);
        switch (weekIndex) {
            case 1:
                week = "一";
                break;
            case 2:
                week = "二";
                break;
            case 3:
                week = "三";
                break;
            case 4:
                week = "四";
                break;
            case 5:
                week = "五";
                break;
            case 6:
                week = "六";
                break;
            case 7:
                week = "日";
                break;
        }
        return week;
    }

    /**
     * 根据指定的日期字符串获取年月日
     *
     * @param datas 指定的日期字符串(yyyy-MM-dd 或 yyyy/MM/dd)
     * @param type  0:年，1：月，2：日
     * @return
     */
    public static String getData(String datas, int type) {
        String data = null;
        String str[] = null;
        if (datas.contains("-")) {
            str = datas.split("-");
        } else if (datas.contains("/")) {
            str = datas.split("/");
        }
        switch (type) {
            case 0:
                data = str[0];
                break;
            case 1:
                data = str[1];
                break;
            case 2:
                data = str[2];
        }
        return data;
    }

    /**
     * 日历生成
     *
     * @param str 日期字符串
     * @return 根据日期生成的日历
     */
    public static Calendar getCalendarByDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 浮点数格式化
     *
     * @param value
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String doubleFormat(double value) {
        return String.format("%.2f", value);
    }

    /**
     * 浮点数格式化
     *
     * @param value
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String floatFormat(float value) {
        return String.format("%.1f", value);
    }

    /**
     * 小数取余
     *
     * @param value
     * @param newScale
     * @return
     */
    public static BigDecimal doubleFormat(double value, int newScale) {
        return new BigDecimal(value).setScale(newScale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 小数四舍五入取整
     *
     * @param value
     * @return
     */
    public static int doubleFormatInt(double value) {
        return doubleFormat(value, 0).intValue();
    }

    /**
     * 返回是或否
     *
     * @param yesOrNo
     * @return
     */
    public static String getStringBuYN(String yesOrNo) {
        if ("1".equals(yesOrNo)) {
            return "是";
        } else {
            return "否";
        }
    }

    /**
     * 获取CID
     *
     * @param context
     * @return
     */
    public static String getCid(Context context) {
        return MD5Utils.compute(getIMEI(context) + getLocalMacAddressFromIp(context) + getAndroidId(context));
    }

    /**
     * 获取ANDROID_ID
     *
     * @param var0
     * @return
     */
    public static String getAndroidId(Context var0) {
        String var1 = "";
        var1 = Settings.Secure.getString(var0.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(var1)) {
            var1 = "";
        }

        return var1;
    }

    /**
     * 获取IMSI
     *
     * @param context
     */
    public static String getIMSI(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = telManager.getSubscriberId();
        JCLoger.debug("imsi:" + imsi);
        return imsi;
    }

    /**
     * 获取IMEI
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        return imei;
    }

    /**
     * 根据IP获取本地Mac
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static String getLocalMacAddressFromIp(Context context) {
        String mac_s = "";
        try {
            byte[] mac;
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress
                    .getByName(getLocalIpAddress()));
            mac = ne.getHardwareAddress();
            mac_s = byte2hex(mac);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(mac_s)) {
            mac_s = getIMEI(context);
        }
        JCLoger.debug("mac_s:" + mac_s);
        return mac_s;
    }

    public static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                hs = hs.append(stmp);
            }
        }
        return String.valueOf(hs);
    }

    /**
     * 获取本地IP
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            // 执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null
                    && line.contains(filter) == false) {
                // result += line;
            }

            result = line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断WIFI是否连接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 得到sharedPreferences的key的值
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getAppInfo(Context context, String key,
                                    String defaultValue) {
        return getAppInfo(context, "", key, defaultValue);
    }

    /**
     * 得带sharedPreferences的key的值
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getAppInfo(Context context, String prefix, String key,
                                    String defaultValue) {
        SharedPreferences app_info = context.getSharedPreferences(
                Constants.PreferenceName, 0);
        return app_info.getString(prefix + key, defaultValue);
    }

    /**
     * 得到sharedPreferences的key的bool值
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getAppInfo(Context context, String key,
                                     boolean defaultValue) {
        return getAppInfo(context, "", key, defaultValue);
    }

    /**
     * 得带sharedPreferences的key的bool值
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getAppInfo(Context context, String prefix, String key,
                                     boolean defaultValue) {
        SharedPreferences app_info = context.getSharedPreferences(
                Constants.PreferenceName, 0);
        return app_info.getBoolean(prefix + key, defaultValue);
    }

    /**
     * 设置sharedPreferences的key的值
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setAppInfo(Context context, String key, String value) {
        setAppInfo(context, "", key, value);
    }

    /**
     * 设置sharedPreferences的key的值
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setAppInfo(Context context, String prefix, String key,
                                  String value) {
        if (!"".equals(value)) {
            SharedPreferences app_info = context.getSharedPreferences(Constants.PreferenceName, 0);
            Editor edit = app_info.edit();
            edit.putString(key, value);
            edit.commit();
            edit = null;
        }
    }

    /**
     * 设置sharedPreferences的key的bool值
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setAppInfo(Context context, String prefix, String key,
                                  boolean value) {
        SharedPreferences app_info = context.getSharedPreferences(
                Constants.PreferenceName, 0);
        Editor edit = app_info.edit();
        edit.putBoolean(key, value);
        edit.commit();
        edit = null;

    }

    /**
     * 通过map来保存数据
     *
     * @param context
     * @param map
     */
    public static void setAppInfoByMap(Context context, String prefix,
                                       HashMap<String, String> map) {
        if (map != null) {
            SharedPreferences app_info = context.getSharedPreferences(
                    Constants.PreferenceName, 0);
            Editor edit = app_info.edit();
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                edit.putString(prefix + key, map.get(key));
            }
            edit.commit();
        }
    }

    /**
     * 将string 转换为逗号隔开的数字。
     *
     * @param indext   表示几位以逗号隔开
     * @param accuracy 精确到小数点后面几位
     */
    public static String formatTosepara(String data, int indext, int accuracy) {
        if (StringUtils.isEmpty(data)) {
            return "0.00";
        }
        if (data.charAt(0) == '.') {
            data = "0" + data;
        }

        Double num;
        try {
            num = parseDouble(data);
        } catch (Exception e) {
            return null;
        }
        StringBuffer buff = new StringBuffer();
        buff.append("#,");
        for (int i = 1; i < indext; i++) {
            buff.append("#");
        }
        buff.append("0.");
        for (int i = 0; i < accuracy; i++) {
            buff.append("0");
        }
        DecimalFormat df = new DecimalFormat(buff.toString());
        return df.format(num);
    }

    /**
     * 保留小数点后面两位。
     *
     * @param data 传入的字符串
     */
    public static String formatFloat(String data) {
        try {
            if (StringUtils.isEmpty(data)) {
                return "0.00";
            }
            DecimalFormat df = new DecimalFormat("0.00");
            Double d = parseDouble(data);
            return df.format(d);
        } catch (Exception e) {
            return "0.00";
        }

    }

    /**
     * 保留小数点。
     *
     * @param data 传入的字符串
     */
    public static String formatFloat(Double data) {
        Double b = Math.floor(data * 10) / 10;
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(b);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取IP地址
     *
     * @param context
     * @return
     */
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 获取通知栏权限是否开启
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean isNotificationEnabled(Context context) {
        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
      /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void requestPermission(Context context, String action) {
        // TODO Auto-generated method stub
        // 6.0以上系统才可以判断权限
        // 进入设置系统应用权限界面
        Intent intent = new Intent(action);
        context.startActivity(intent);
    }

    public static double str2dou(String str) {
        if (StringUtils.isEmpty(str)) {
            return 0.00;
        } else {
            try {
                return Double.parseDouble(str);
            } catch (Exception e) {
                return 0.00;
            }
        }
    }

    public static void setViewPageHeight(ViewPager viewPager, int height) {

        ViewGroup.LayoutParams params = viewPager.getLayoutParams();

        int childSize = viewPager.getChildCount();
        int maxHeight = 0;
        for (int i = 0; i < childSize; i++) {
            View child = viewPager.getChildAt(i);
            child.measure(0, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            if (child.getMeasuredHeight() > maxHeight) {
                maxHeight = child.getMeasuredHeight();
            }
        }

        if (maxHeight > 0) {
            params.height = maxHeight + height;
            viewPager.setLayoutParams(params);
        }
    }

    /**
     * inputStream转String
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 判断手机网络是否可用
     *
     * @param context 上下文
     * @return 返回手机网络状态
     */
    public static boolean isNetWorkOK(Context context) {
        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cwjManager.getActiveNetworkInfo().isAvailable();
    }

    /**
     * 时间对比
     *
     * @param start
     * @param end
     * @return
     */
    public static boolean isCurrentDate(String start, String end) {
        if (StringUtils.isEmpty(start)) {
            return false;
        }
        boolean current = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.SimpleDateFormat);
        try {
            Date dt1 = dateFormat.parse(start);
            Date dt2 = dateFormat.parse(end);
            if (dt1.getTime() > dt2.getTime()) {
                current = false;
            } else {
                current = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return current;
    }

}
