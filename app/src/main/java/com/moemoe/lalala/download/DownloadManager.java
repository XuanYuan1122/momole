package com.moemoe.lalala.download;


import com.app.Utils;
import com.app.common.Callback;
import com.app.common.task.PriorityExecutor;
import com.app.http.RequestParams;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public final class DownloadManager {

    private static DownloadManager instance;

    private final static int MAX_DOWNLOAD_THREAD = 2; // 有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
    private final Executor executor = new PriorityExecutor(MAX_DOWNLOAD_THREAD, true);
    //private final List<DownloadInfo> downloadInfoList = new ArrayList<DownloadInfo>();
    private final Map<String,DownloadInfo> downloadInfoMap = new HashMap<>();
    private final ConcurrentHashMap<DownloadInfo, DownloadCallback>
            callbackMap = new ConcurrentHashMap<DownloadInfo, DownloadCallback>(5);

    private DownloadManager() {
    }

    static DownloadManager getInstance() {
        if (instance == null) {
            synchronized (DownloadManager.class) {
                if (instance == null) {
                    instance = new DownloadManager();
                }
            }
        }
        return instance;
    }

    public int getDownloadListCount() {
        return downloadInfoMap.size();
    }

    public DownloadInfo getDownloadInfo(String index) {
        return downloadInfoMap.get(index);
    }

    public synchronized void startDownload(DownloadInfo downloadInfo,
                                           DownloadViewHolder viewHolder) {

        String fileSavePath = new File(downloadInfo.getFileSavePath()).getAbsolutePath();
        // start downloading
        if (viewHolder == null) {
            viewHolder = new DefaultDownloadViewHolder(null, downloadInfo);
        }
        DownloadCallback callback = new DownloadCallback(viewHolder);
        callback.setDownloadManager(this);
        callback.switchViewHolder(viewHolder);
        RequestParams params = new RequestParams(downloadInfo.getUrl());
        params.setAutoResume(downloadInfo.isAutoResume());
        params.setAutoRename(downloadInfo.isAutoRename());
        params.setSaveFilePath(downloadInfo.getFileSavePath());
        params.setExecutor(executor);
        params.setCancelFast(true);
        Callback.Cancelable cancelable = Utils.http().get(params, callback);
        callback.setCancelable(cancelable);
        callbackMap.put(downloadInfo, callback);

        if (downloadInfoMap.containsKey(downloadInfo.getUrl())) {
            downloadInfoMap.remove(downloadInfo.getUrl());
        }
        downloadInfoMap.put(downloadInfo.getUrl(), downloadInfo);
    }

    public void stopDownload(String index) {
        DownloadInfo downloadInfo = downloadInfoMap.get(index);
        stopDownload(downloadInfo);
    }

    public void stopDownload(DownloadInfo downloadInfo) {
        Callback.Cancelable cancelable = callbackMap.get(downloadInfo);
        if (cancelable != null) {
            cancelable.cancel();
        }
    }

    public void stopAllDownload() {
        for (DownloadInfo downloadInfo : downloadInfoMap.values()) {
            Callback.Cancelable cancelable = callbackMap.get(downloadInfo);
            if (cancelable != null) {
                cancelable.cancel();
            }
        }
    }

    public void removeDownload(String url){
        DownloadInfo downloadInfo = downloadInfoMap.get(url);
        stopDownload(downloadInfo);
        downloadInfoMap.remove(url);
    }

    public void removeDownload(DownloadInfo downloadInfo){
        stopDownload(downloadInfo);
        downloadInfoMap.remove(downloadInfo.getId());
    }
}
