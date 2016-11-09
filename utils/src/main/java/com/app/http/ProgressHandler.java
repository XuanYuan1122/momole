package com.app.http;

/**
 * Created by Haru on 2016/4/12 0012.
 * 进度控制接口, updateProgress方式中ProgressCallback#onLoading.
 * 默认速率300毫秒调用一次.
 */
public interface ProgressHandler {
    /**
     * @param total
     * @param current
     * @param forceUpdateUI
     * @return continue
     */
    boolean updateProgress(long total, long current, boolean forceUpdateUI);
}
