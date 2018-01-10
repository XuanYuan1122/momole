package com.moemoe.lalala.utils.retrofit.parser;

import okhttp3.HttpUrl;

/**
 * 默认解析器
 * Created by yi on 2018/1/10.
 */

public class DefaultUrlParser implements UrlParser {

    @Override
    public HttpUrl parseUrl(HttpUrl domainUrl, HttpUrl url) {
        if(null == domainUrl) return url;
        return url.newBuilder()
                .scheme(domainUrl.scheme())
                .host(domainUrl.host())
                .port(domainUrl.port())
                .build();
    }
}
