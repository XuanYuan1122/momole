package com.app.http.app;

import com.app.http.request.UriRequest;

/**
 * Created by Haru on 2016/4/12 0012.
 * 拦截请求响应(在后台线程工作).
 * <p>
 * 用法: 请求的callback参数同时实现InterceptRequestListener
 */
public interface InterceptRequestListener {

    void beforeRequest(UriRequest request) throws Throwable;

    void afterRequest(UriRequest request) throws Throwable;
}