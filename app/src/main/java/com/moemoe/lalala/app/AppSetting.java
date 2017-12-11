package com.moemoe.lalala.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 应用类缓存全局设置
 * Created by yi on 2016/11/28.
 */

public class AppSetting {
    public static boolean IS_DOWNLOAD_LOW_IN_3G = true;
    public static boolean IS_EDITOR_VERSION = false;
    public static boolean OpenUmeng = true;
    public static boolean isRunning = false;
    public static boolean isFirstLauncherToday = false;
    public static boolean isEnterEventToday = false;
    public static boolean isShowBackSchoolAll = false;
    public static boolean isLoadDone = false;
    public static String sCurChatId = "";
    public static boolean TXBB = false;
    public static double LAT;
    public static double LON;


    public static String CHANNEL;
    public static int VERSION_CODE;

    public static void initDeviceInfo(Context context){
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            CHANNEL = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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

        PackageInfo info;
        try {
            info = pm.getPackageInfo(context.getPackageName(), 0);
            VERSION_CODE = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}
