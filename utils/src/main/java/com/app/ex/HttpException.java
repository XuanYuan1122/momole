package com.app.ex;

import android.text.TextUtils;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public class HttpException extends BaseException {
    private static final long serialVersionUID = 1L;

    private int code;
    private String customMessage;
    private String result;

    /**
     * @param code          The http response status code, 0 if the http request error and has no response.
     * @param detailMessage The http response message.
     */
    public HttpException(int code, String detailMessage) {
        super(detailMessage);
        this.code = code;
    }

    public void setMessage(String message) {
        this.customMessage = message;
    }

    /**
     * @return The http response status code, 0 if the http request error and has no response.
     */
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        if (!TextUtils.isEmpty(customMessage)) {
            return customMessage;
        } else {
            return super.getMessage();
        }
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "code: " + code + ", msg: " + getMessage() + ", result: " + result;
    }
}
