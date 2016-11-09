package com.moemoe.lalala.download;

import android.view.View;

import com.app.Utils;
import com.app.common.Callback;

import java.io.File;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public abstract class DownloadViewHolder {

    protected DownloadInfo downloadInfo;

    public DownloadViewHolder(View view, DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
        Utils.view().inject(this, view);
    }

    public final DownloadInfo getDownloadInfo() {
        return downloadInfo;
    }

    public void update(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }

    public abstract void onWaiting();

    public abstract void onStarted();

    public abstract void onLoading(long total, long current);

    public abstract void onSuccess(File result);

    public abstract void onError(Throwable ex, boolean isOnCallback);

    public abstract void onCancelled(Callback.CancelledException cex);
}
