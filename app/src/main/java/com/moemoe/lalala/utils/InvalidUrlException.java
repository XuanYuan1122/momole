package com.moemoe.lalala.utils;

import android.text.TextUtils;

/**
 *
 * Created by yi on 2018/1/10.
 */

public class InvalidUrlException extends RuntimeException {

    public InvalidUrlException(String url) {
        super("You've configured an invalid url : " + (TextUtils.isEmpty(url) ? "EMPTY_OR_NULL_URL" : url));
    }
}
