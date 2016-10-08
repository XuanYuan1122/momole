package com.app.http;

import com.app.common.util.LogUtil;
import com.app.http.app.RequestTracker;
import com.app.http.request.UriRequest;

/**
 * Created by Haru on 2016/4/12 0012.
 */
/*package*/ final class RequestTrackerWrapper implements RequestTracker {
    private final RequestTracker base;

    public RequestTrackerWrapper(RequestTracker base) {
        this.base = base;
    }

    @Override
    public void onWaiting(UriRequest request) {
        try {
            base.onWaiting(request);
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
    }

    @Override
    public void onStart(UriRequest request) {
        try {
            base.onStart(request);
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
    }

    @Override
    public void onCache(UriRequest request) {
        try {
            base.onCache(request);
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
    }

    @Override
    public void onSuccess(UriRequest request) {
        try {
            base.onSuccess(request);
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
    }

    @Override
    public void onCancelled(UriRequest request) {
        try {
            base.onCancelled(request);
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
    }

    @Override
    public void onError(UriRequest request, Throwable ex, boolean isCallbackError) {
        try {
            base.onError(request, ex, isCallbackError);
        } catch (Throwable exOnError) {
            LogUtil.e(exOnError.getMessage(), exOnError);
        }
    }

    @Override
    public void onFinished(UriRequest request) {
        try {
            base.onFinished(request);
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
    }
}
