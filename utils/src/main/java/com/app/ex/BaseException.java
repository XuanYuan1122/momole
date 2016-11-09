package com.app.ex;

import java.io.IOException;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public class BaseException extends IOException {
    private static final long serialVersionUID = 1L;

    public BaseException() {
        super();
    }

    public BaseException(String detailMessage) {
        super(detailMessage);
    }

    public BaseException(String detailMessage, Throwable throwable) {
        super(detailMessage);
        this.initCause(throwable);
    }

    public BaseException(Throwable throwable) {
        super(throwable.getMessage());
        this.initCause(throwable);
    }
}
