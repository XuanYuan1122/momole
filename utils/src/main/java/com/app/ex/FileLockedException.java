package com.app.ex;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public class FileLockedException extends BaseException {
    private static final long serialVersionUID = 1L;

    public FileLockedException(String detailMessage) {
        super(detailMessage);
    }
}
