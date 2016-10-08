package com.moemoe.lalala.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.moemoe.lalala.R;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Haru on 2016/5/1 0001.
 */
public class AppSetting {
    public static boolean IS_DOWNLOAD_LOW_IN_3G = true;
    public static boolean IS_DEVELOP_MODE = false;
    public static boolean IS_EDITOR_VERSION = false;
    public static boolean OpenUmeng = true;
    public static boolean isDebug = false;
    public static boolean isRunning = false;
    public static boolean isFirstLauncherToday = false;
    public static boolean isEnterEventToday = false;

    public static String IMEI;
    public static String CHANNEL;
    public static int VERSION_CODE;
    public static boolean sIs7Tablet;
    public static boolean sIsPhone;
    public static boolean sIsXTablet;
    public static int DENSITY_DPI;
    public static float DENSITY;
    public static int SCREEN_WIDTH, SCREEN_HEIGHT;
    public static float X_DPI, Y_DPI;
    public static final int CURSOR_LOADER_UPDATE_THROTTLE = 500;
    /**
     *
     */
    public static void initDeviceInfo(Context context){
        IMEI =  ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if(TextUtils.isEmpty(IMEI)){
            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);

            IMEI = wifi.getConnectionInfo().getMacAddress();
        }
        if(TextUtils.isEmpty(IMEI)){
            // 测试机
            IMEI = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        if(TextUtils.isEmpty(IMEI)){
            // 无法判断唯一设备，随机数字吧
            IMEI = "nada" + new Random(System.currentTimeMillis()).nextInt() % 1000000000;
        }

       // String channel = getChannel(context);
      //  if(!TextUtils.isEmpty(channel)){
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            CHANNEL = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        sIs7Tablet = context.getResources().getBoolean(R.bool.is7inchScreen);
        sIsXTablet = context.getResources().getBoolean(R.bool.isXLargeScreen);
        sIsPhone = !sIs7Tablet && !sIsXTablet;


        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        DENSITY_DPI = metrics.densityDpi;
        DENSITY = metrics.density;
        if (sIsPhone) {
            SCREEN_WIDTH = Math.min(metrics.widthPixels, metrics.heightPixels);
            SCREEN_HEIGHT = Math.max(metrics.widthPixels, metrics.heightPixels);
            X_DPI = Math.min(metrics.xdpi, metrics.ydpi);
            Y_DPI = Math.max(metrics.xdpi, metrics.ydpi);
        } else {
            SCREEN_WIDTH = Math.max(metrics.widthPixels, metrics.heightPixels);
            SCREEN_HEIGHT = Math.min(metrics.widthPixels, metrics.heightPixels);
            X_DPI = Math.max(metrics.xdpi, metrics.ydpi);
            Y_DPI = Math.min(metrics.xdpi, metrics.ydpi);
        }
        getPackageInfo(context);
    }

    /**
     * 获取包内信息
     * @param context
     * @author Ben
     */
    private static void getPackageInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            VERSION_CODE = info.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
        }
    }

//    /**
//     * 获取渠道
//     * @param context
//     * @return
//     */
//    private static String getChannel(Context context){
//        ApplicationInfo appInfo = context.getApplicationInfo();
//        String sourceDir = appInfo.sourceDir;
//        String ret = "";
//        ZipFile zipfile = null;
//
//        try {
//            zipfile = new ZipFile(sourceDir);
//            Enumeration<?> entries = zipfile.entries();
//            while(entries.hasMoreElements()){
//                ZipEntry entry  = (ZipEntry) entries.nextElement();
//                String entryName = entry.getName();
//                if(entryName.contains("vendor")){
//                    ret = entryName;
//                    break;
//                }
//            }
//        } catch (IOException e) {
//        } finally {
//            if(zipfile != null){
//                try {
//                    zipfile.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//
//        String[] split = ret.split("_");
//        if(split != null && split.length >= 2){
//            return ret.substring(split[0].length()+1);
//        } else {
//            return "";
//        }
//    }
}
