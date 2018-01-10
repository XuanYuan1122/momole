package com.moemoe.lalala.utils;

import okhttp3.HttpUrl;

/**
 *
 * Created by yi on 2018/1/10.
 */

public class UrlUtils {

    private UrlUtils() {
        throw new IllegalStateException("do not instantiation me");
    }

    public static HttpUrl checkUrl(String url) {
        HttpUrl parseUrl = HttpUrl.parse(url);
        if (null == parseUrl) {
            throw new InvalidUrlException(url);
        } else {
            return parseUrl;
        }
    }
}
