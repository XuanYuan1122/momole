package com.app.http.app;

import com.app.common.Callback;
import com.app.common.util.LogUtil;
import com.app.ex.HttpException;
import com.app.http.HttpMethod;
import com.app.http.request.UriRequest;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashSet;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public final class HttpRetryHandler {
    protected int maxRetryCount = 2;

    private static HashSet<Class<?>> blackList = new HashSet<Class<?>>();

    static {
        blackList.add(HttpException.class);
        blackList.add(Callback.CancelledException.class);
        blackList.add(MalformedURLException.class);
        blackList.add(URISyntaxException.class);
        blackList.add(NoRouteToHostException.class);
        blackList.add(PortUnreachableException.class);
        blackList.add(ProtocolException.class);
        blackList.add(NullPointerException.class);
        blackList.add(FileNotFoundException.class);
        blackList.add(JSONException.class);
        blackList.add(UnknownHostException.class);
        blackList.add(IllegalArgumentException.class);
    }

    public HttpRetryHandler() {
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public boolean retryRequest(Throwable ex, int count, UriRequest request) {

        LogUtil.w(ex.getMessage(), ex);

        if (count > maxRetryCount || request == null) {
            LogUtil.w("The Max Retry times has been reached!");
            return false;
        }

        if (!HttpMethod.permitsRetry(request.getParams().getMethod())) {
            LogUtil.w("The Request Method can not be retried.");
            return false;
        }

        if (blackList.contains(ex.getClass())) {
            LogUtil.w("The Exception can not be retried.");
            return false;
        }

        return true;
    }
}
