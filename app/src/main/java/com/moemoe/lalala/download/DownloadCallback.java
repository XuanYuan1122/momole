package com.moemoe.lalala.download;

import com.app.common.Callback;
import com.app.common.util.LogUtil;
import com.app.ex.DbException;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Haru on 2016/4/27 0027.
 */
class DownloadCallback implements
        Callback.CommonCallback<File>,
        Callback.ProgressCallback<File>,
        Callback.Cancelable{

    private DownloadInfo downloadInfo;
    private WeakReference<DownloadViewHolder> viewHolderRef;
    private DownloadManager downloadManager;
    private boolean cancelled = false;
    private Cancelable cancelable;

    public DownloadCallback(DownloadViewHolder viewHolder) {
        this.switchViewHolder(viewHolder);
    }

    public boolean switchViewHolder(DownloadViewHolder viewHolder) {
        if (viewHolder == null) return false;

        synchronized (DownloadCallback.class) {
            if (downloadInfo != null) {
                if (this.isStopped()) {
                    return false;
                }
            }
            this.downloadInfo = viewHolder.getDownloadInfo();
            this.viewHolderRef = new WeakReference<DownloadViewHolder>(viewHolder);
        }
        return true;
    }

    public void setDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    public void setCancelable(Cancelable cancelable) {
        this.cancelable = cancelable;
    }

    private DownloadViewHolder getViewHolder() {
        if (viewHolderRef == null) return null;
        DownloadViewHolder viewHolder = viewHolderRef.get();
        if (viewHolder != null) {
            DownloadInfo downloadInfo = viewHolder.getDownloadInfo();
            if (this.downloadInfo != null && this.downloadInfo.equals(downloadInfo)) {
                return viewHolder;
            }
        }
        return null;
    }

    @Override
    public void cancel() {
        cancelled = true;
        if (cancelable != null) {
            cancelable.cancel();
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void onWaiting() {
        downloadInfo.setState(DownloadState.WAITING);
        DownloadViewHolder viewHolder = this.getViewHolder();
        if (viewHolder != null) {
            viewHolder.onWaiting();
        }
    }

    @Override
    public void onStarted() {
        downloadInfo.setState(DownloadState.STARTED);
        DownloadViewHolder viewHolder = this.getViewHolder();
        if (viewHolder != null) {
            viewHolder.onStarted();
        }
    }

    @Override
    public void onLoading(long total, long current, boolean isDownloading) {
        if (isDownloading) {
            downloadInfo.setState(DownloadState.STARTED);
            downloadInfo.setFileLength(total);
            downloadInfo.setProgress((int) (current * 100 / total));
            DownloadViewHolder viewHolder = this.getViewHolder();
            if (viewHolder != null) {
                viewHolder.onLoading(total, current);
            }
        }
    }

    @Override
    public void onSuccess(File result) {
        synchronized (DownloadCallback.class) {
            downloadInfo.setState(DownloadState.FINISHED);
            DownloadViewHolder viewHolder = this.getViewHolder();
            if (viewHolder != null) {
                viewHolder.onSuccess(result);
            }
        }
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        synchronized (DownloadCallback.class) {
            downloadInfo.setState(DownloadState.ERROR);
            DownloadViewHolder viewHolder = this.getViewHolder();
            if (viewHolder != null) {
                viewHolder.onError(ex, isOnCallback);
            }
        }
    }

    @Override
    public void onCancelled(CancelledException cex) {
        synchronized (DownloadCallback.class) {
            downloadInfo.setState(DownloadState.STOPPED);
            DownloadViewHolder viewHolder = this.getViewHolder();
            if (viewHolder != null) {
                viewHolder.onCancelled(cex);
            }
        }
    }

    @Override
    public void onFinished() {
        cancelled = false;
    }

    private boolean isStopped() {
        DownloadState state = downloadInfo.getState();
        return isCancelled() || state.value() > DownloadState.STARTED.value();
    }
}
