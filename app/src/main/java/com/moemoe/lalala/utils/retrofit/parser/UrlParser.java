package com.moemoe.lalala.utils.retrofit.parser;

import okhttp3.HttpUrl;

/**
 * Url解析器
 * Created by yi on 2018/1/10.
 */

public interface UrlParser {

    /**
     * 将请求url替换为映射url 达到动态替换
     */
    HttpUrl parseUrl(HttpUrl domainUrl,HttpUrl url);
}
