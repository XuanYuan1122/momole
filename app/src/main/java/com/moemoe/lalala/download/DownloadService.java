package com.moemoe.lalala.download;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.app.Utils;
import com.app.db.converter.ColumnConverterFactory;

import java.util.List;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class DownloadService extends Service {

    static {
        // 注册DownloadState在数据库中的值类型映射
        ColumnConverterFactory.registerColumnConverter(DownloadState.class, new DownloadStateConverter());
    }

    private static DownloadManager DOWNLOAD_MANAGER;

    public synchronized static DownloadManager getDownloadManager() {
        if (!DownloadService.isServiceRunning(Utils.app())) {
            Intent downloadSvr = new Intent(Utils.app(), DownloadService.class);
            Utils.app().startService(downloadSvr);
        }
        if (DownloadService.DOWNLOAD_MANAGER == null) {
            DownloadService.DOWNLOAD_MANAGER = DownloadManager.getInstance();
        }
        return DOWNLOAD_MANAGER;
    }

    public DownloadService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (DOWNLOAD_MANAGER != null) {
            DOWNLOAD_MANAGER.stopAllDownload();
        }
        super.onDestroy();
    }

    public static boolean isServiceRunning(Context context) {
        boolean isRunning = false;

        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (serviceList == null || serviceList.size() == 0) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(DownloadService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
