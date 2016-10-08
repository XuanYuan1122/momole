package com.app.http.body;

import com.app.http.ProgressHandler;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public interface ProgressBody extends RequestBody {
    void setProgressHandler(ProgressHandler progressHandler);
}
