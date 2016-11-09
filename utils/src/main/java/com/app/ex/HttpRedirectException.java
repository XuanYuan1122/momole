package com.app.ex;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public class HttpRedirectException extends HttpException {
    private static final long serialVersionUID = 1L;

    public HttpRedirectException(int code, String detailMessage, String result) {
        super(code, detailMessage);
        this.setResult(result);
    }
}
