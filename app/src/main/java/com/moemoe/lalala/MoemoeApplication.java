package com.moemoe.lalala;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.app.Utils;
import com.app.view.DbManager;
import com.igexin.sdk.PushManager;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.thirdopen.ThirdPartySDKManager;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.IConstants;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.UnCaughtException;

import java.io.File;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Haru on 2016/4/26 0026.
 */
public class MoemoeApplication extends Application {

    /**
     * 当前进程允许的最大内存
     */
    public static long PROCESS_MEMORY_LIMIT;
    private static MoemoeApplication mInstance = null;
    public static  DbManager.DaoConfig sDaoConfig;
    public static int activityVisiableCount = 0;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private LinkedList<Activity> mActivitiesList = new LinkedList<Activity>();
    public LinkedList<Activity> getActivitiesList() {
        return mActivitiesList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        Utils.Control.init(this);
        Utils.Control.setDebug(AppSetting.isDebug);
        File cacheDirectory = getApplicationContext().getCacheDir();
        if(AppSetting.isDebug){
            Otaku.setup(IConstants.debugBaseUrl,cacheDirectory);
        }else {
            Otaku.setup(IConstants.baseUrl, cacheDirectory);
        }
        initMemoryParameter();
//        ThirdPartySDKManager.getInstance(this).init();
//        File cacheDirectory = getApplicationContext().getCacheDir();
//        if(AppSetting.isDebug){
//            Otaku.setup(IConstants.debugBaseUrl,cacheDirectory);
//        }else {
//            Otaku.setup(IConstants.baseUrl, cacheDirectory);
//        }
//        StorageUtils.initialStorageDir(this);
//        EncoderUtils.init(this);
//        IntentUtils.init(this);
        UnCaughtException caughtException = UnCaughtException.getInstance();
        caughtException.init(getApplicationContext());
//        AppSetting.initDeviceInfo(this);
//        PushManager.getInstance().initialize(this.getApplicationContext());
//        MoemoeApplication.sDaoConfig = new DbManager.DaoConfig()
//                .setDbName("netaInfo")
//                .setDbVersion(1)
//                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
//                    @Override
//                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
//
//                    }
//                });
    }

    private String initMemoryParameter() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memInfo);
        Runtime runtime = Runtime.getRuntime();
        PROCESS_MEMORY_LIMIT = runtime.maxMemory();
        if (memInfo.availMem >= 48 * 1024 * 1024 && PROCESS_MEMORY_LIMIT >= 48 * 1024 * 1024) {
            BitmapUtils.DEFAULT_BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
        } else {
            BitmapUtils.DEFAULT_BITMAP_CONFIG = Bitmap.Config.RGB_565;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("Default bitmap config:" + BitmapUtils.DEFAULT_BITMAP_CONFIG.toString());
        sb.append(" AvailMem:" + memInfo.availMem);
        sb.append(" Threshold:" + memInfo.threshold);
        sb.append(" LowMemory:" + memInfo.lowMemory);
        sb.append(" ProMemLim:" + PROCESS_MEMORY_LIMIT);
        sb.append(" ProTotalMem:" + runtime.totalMemory());
        return sb.toString();
    }

    public boolean isAppRunningFront() {
        return activityVisiableCount > 0;
    }

    public void setIsAppRunningFront(boolean isAppRunningFront) {
        if (isAppRunningFront) {
            activityVisiableCount++;
            if(mTimerTask != null && mTimer != null){
                mTimer.cancel();
                mTimer = null;
                mTimerTask.cancel();
                mTimerTask = null;
            }
        } else if (activityVisiableCount > 0) {
            activityVisiableCount--;
            if(!isAppRunningFront()){
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        finishAllActivity();
                    }
                };
                mTimer = new Timer();
                mTimer.schedule(mTimerTask,5 * 60 * 1000);
            }
        }
    }

    public void addActivity(Activity activity) {
        synchronized (mActivitiesList) {
            mActivitiesList.add(0,activity);
        }
    }

    public void removeActivity(Activity activity) {
        synchronized (mActivitiesList) {
            mActivitiesList.remove(activity);
        }
    }

    private void finishAllActivity(){
        for(Activity activity : mActivitiesList){
            activity.finish();
        }
    }

    public static MoemoeApplication getInstance() {
        if (mInstance == null) {
            mInstance = new MoemoeApplication();
        }
        return mInstance;
    }
}
