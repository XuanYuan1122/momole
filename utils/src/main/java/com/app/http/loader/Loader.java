package com.app.http.loader;

import android.text.TextUtils;

import com.app.cache.DiskCacheEntity;
import com.app.cache.LruDiskCache;
import com.app.http.ProgressHandler;
import com.app.http.RequestParams;
import com.app.http.app.RequestTracker;
import com.app.http.request.UriRequest;

import java.io.InputStream;
import java.util.Date;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public abstract class Loader<T> {
    protected RequestParams params;
    protected RequestTracker tracker;
    protected ProgressHandler progressHandler;

    public void setParams(final RequestParams params) {
        this.params = params;
    }

    public void setProgressHandler(final ProgressHandler callbackHandler) {
        this.progressHandler = callbackHandler;
    }

    public void setResponseTracker(RequestTracker tracker) {
        this.tracker = tracker;
    }

    public RequestTracker getResponseTracker() {
        return this.tracker;
    }

    protected void saveStringCache(UriRequest request, String resultStr) {
        if (!TextUtils.isEmpty(resultStr)) {
            DiskCacheEntity entity = new DiskCacheEntity();
            entity.setKey(request.getCacheKey());
            entity.setLastAccess(System.currentTimeMillis());
            entity.setEtag(request.getETag());
            entity.setExpires(request.getExpiration());
            entity.setLastModify(new Date(request.getLastModified()));
            entity.setTextContent(resultStr);
            LruDiskCache.getDiskCache(request.getParams().getCacheDirName()).put(entity);
        }
    }

    public abstract Loader<T> newInstance();

    public abstract T load(final InputStream in) throws Throwable;

    public abstract T load(final UriRequest request) throws Throwable;

    public abstract T loadFromCache(final DiskCacheEntity cacheEntity) throws Throwable;

    public abstract void save2Cache(final UriRequest request);
}
