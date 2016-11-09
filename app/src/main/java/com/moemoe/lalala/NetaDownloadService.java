package com.moemoe.lalala;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.app.Utils;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.filedownloader.DownloadFileInfo;
import com.moemoe.lalala.utils.filedownloader.FileDownloader;
import com.moemoe.lalala.utils.filedownloader.listener.OnRetryableFileDownloadStatusListener;
import com.moemoe.lalala.utils.filedownloader.util.MathUtil;

import java.io.File;
import java.util.List;

/**
 * Created by yi on 2016/10/12.
 */

public class NetaDownloadService extends Service implements OnRetryableFileDownloadStatusListener{

    private static final int DOWNLOAD_NOTIFY_ID = 667668;
    private NotificationManager mNotificationManager = null;
    private NotificationCompat.Builder mBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FileDownloader.registerDownloadStatusListener(this);
        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(mBuilder != null){
            mNotificationManager.cancel(DOWNLOAD_NOTIFY_ID);
        }
    }

    public synchronized static void startService() {
        if (!isServiceRunning(Utils.app())) {
            Intent downloadSvr = new Intent(MoemoeApplication.getInstance(), NetaDownloadService.class);
            Utils.app().startService(downloadSvr);
        }
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
            if (serviceList.get(i).service.getClassName().equals(NetaDownloadService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FileDownloader.unregisterDownloadStatusListener(this);
        FileDownloader.pauseAll();
    }

    @Override
    public void onFileDownloadStatusRetrying(DownloadFileInfo downloadFileInfo, int retryTimes) {

    }

    @Override
    public void onFileDownloadStatusWaiting(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {
        if(downloadFileInfo.getUrl().equals(NewDocDetailActivity.gameUrl)){
            Bitmap btm = BitmapFactory.decodeResource(NetaDownloadService.this.getResources(),
                    R.drawable.game_icon);
            mBuilder = new NotificationCompat.Builder(
                    NetaDownloadService.this)
                    .setSmallIcon(R.drawable.game_icon)
                    .setLargeIcon(btm);
            mBuilder.setTicker(downloadFileInfo.getFileName() + "下载开始!");
            mBuilder.setAutoCancel(true);
            String schema = "neta://com.moemoe.lalala/doc_1.0?"+NewDocDetailActivity.specialId;
            Intent resultIntent = null;
            if (AppSetting.isRunning) {
                resultIntent = IntentUtils.getIntentFromUri(NetaDownloadService.this, Uri.parse(schema));
            } else {
                resultIntent = new Intent(NetaDownloadService.this,
                        SplashActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra("schema", schema);
            }
            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    NetaDownloadService.this, R.string.app_name, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            long totalSize =  downloadFileInfo.getFileSizeLong();
            long downloaded = downloadFileInfo.getDownloadedSizeLong();
            int progress = (int) (downloaded * 100 / totalSize);
            double downloadSize = downloaded / 1024f / 1024;
            double fileSize = totalSize / 1024f / 1024;
//            mBuilder.setContentTitle(downloadFileInfo.getFileName());
//            mBuilder.setContentText(MathUtil.formatNumber(downloadSize)+ "M" + "/" + MathUtil.formatNumber(fileSize)+ "MB");
//            mBuilder.setContentInfo("下载中");
            RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.item_download_notify);
            mRemoteViews.setImageViewResource(R.id.custom_progress_icon, R.drawable.game_icon);
            mRemoteViews.setTextViewText(R.id.tv_custom_progress_title, downloadFileInfo.getFileName());
            mRemoteViews.setTextViewText(R.id.tv_custom_progress_size, MathUtil.formatNumber(downloadSize)+ "M" + "/" + MathUtil.formatNumber(fileSize)+ "MB");
            mRemoteViews.setTextViewText(R.id.tv_custom_progress_status, "下载中");
            mRemoteViews.setProgressBar(R.id.custom_progressbar, 100, progress, false);
            mBuilder.setContent(mRemoteViews);
           // mBuilder.setProgress(100,progress,false);
            mNotificationManager.notify(DOWNLOAD_NOTIFY_ID,mBuilder.build());
        }
    }

    @Override
    public void onFileDownloadStatusDownloading(DownloadFileInfo downloadFileInfo, float downloadSpeed, long remainingTime) {
        if(downloadFileInfo.getUrl().equals(NewDocDetailActivity.gameUrl)){
            if(mBuilder == null) return;
            long totalSize =  downloadFileInfo.getFileSizeLong();
            long downloaded = downloadFileInfo.getDownloadedSizeLong();
            int progress = (int) (downloaded * 100 / totalSize);
            double downloadSize = downloaded / 1024f / 1024;
            double fileSize = totalSize / 1024f / 1024;

           // mBuilder.setContentTitle(downloadFileInfo.getFileName());
         //   mBuilder.setContentText(MathUtil.formatNumber(downloadSize)+ "M" + "/" + MathUtil.formatNumber(fileSize)+ "MB");
          //  mBuilder.setContentInfo(MathUtil.formatNumber(downloadSpeed) + "KB/s   " + StringUtils
         //           .seconds2HH_mm_ss(remainingTime));
         //   mBuilder.setProgress(100,progress,false);

            RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.item_download_notify);
            mRemoteViews.setImageViewResource(R.id.custom_progress_icon, R.drawable.game_icon);
            mRemoteViews.setTextViewText(R.id.tv_custom_progress_title, downloadFileInfo.getFileName());
            mRemoteViews.setTextViewText(R.id.tv_custom_progress_size, MathUtil.formatNumber(downloadSize)+ "M" + "/" + MathUtil.formatNumber(fileSize)+ "MB");
            mRemoteViews.setTextViewText(R.id.tv_custom_progress_status, MathUtil.formatNumber(downloadSpeed) + "KB/s   " + StringUtils
                              .seconds2HH_mm_ss(remainingTime));
            mRemoteViews.setProgressBar(R.id.custom_progressbar, 100, progress, false);
            mBuilder.setContent(mRemoteViews);
            mNotificationManager.notify(DOWNLOAD_NOTIFY_ID,mBuilder.build());
        }
    }

    @Override
    public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
        if(downloadFileInfo.getUrl().equals(NewDocDetailActivity.gameUrl)){
            if(mBuilder == null) return;
            long totalSize =  downloadFileInfo.getFileSizeLong();
            long downloaded = downloadFileInfo.getDownloadedSizeLong();
            int progress = (int) (downloaded * 100 / totalSize);
            double downloadSize = downloaded / 1024f / 1024;
            double fileSize = totalSize / 1024f / 1024;
//            mBuilder.setContentTitle(downloadFileInfo.getFileName());
//            mBuilder.setContentText(MathUtil.formatNumber(downloadSize)+ "M" + "/" + MathUtil.formatNumber(fileSize)+ "MB");
//            mBuilder.setContentInfo("暂停");
//            mBuilder.setProgress(100,progress,false);
            RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.item_download_notify);
            mRemoteViews.setImageViewResource(R.id.custom_progress_icon, R.drawable.game_icon);
            mRemoteViews.setTextViewText(R.id.tv_custom_progress_title, downloadFileInfo.getFileName());
            mRemoteViews.setTextViewText(R.id.tv_custom_progress_size, MathUtil.formatNumber(downloadSize)+ "M" + "/" + MathUtil.formatNumber(fileSize)+ "MB");
            mRemoteViews.setTextViewText(R.id.tv_custom_progress_status,"暂停");
            mRemoteViews.setProgressBar(R.id.custom_progressbar, 100, progress, false);
            mBuilder.setContent(mRemoteViews);
            mNotificationManager.notify(DOWNLOAD_NOTIFY_ID,mBuilder.build());
        }
    }

    @Override
    public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
        if(downloadFileInfo.getUrl().equals(NewDocDetailActivity.gameUrl)){
            mNotificationManager.cancel(DOWNLOAD_NOTIFY_ID);
            Intent installIntent = new Intent();
            installIntent.setAction(Intent.ACTION_VIEW);
            File file = new File(downloadFileInfo.getFileDir(), downloadFileInfo.getFileName());
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            startActivity(installIntent);
        }
    }

    @Override
    public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {
        if(downloadFileInfo.getUrl().equals(NewDocDetailActivity.gameUrl)){
            mNotificationManager.cancel(DOWNLOAD_NOTIFY_ID);
        }
    }
}
