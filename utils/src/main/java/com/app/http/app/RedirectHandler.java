package com.app.http.app;

import com.app.http.RequestParams;
import com.app.http.request.UriRequest;

/**
 * Created by Haru on 2016/4/12 0012.
 * 请求重定向控制接口
 */
public interface RedirectHandler {

    /**
     * 根据请求信息返回自定义重定向的请求参数
     *
     * @param request
     * @return 返回不为null时进行重定向
     */
    RequestParams getRedirectParams(UriRequest request);
}
