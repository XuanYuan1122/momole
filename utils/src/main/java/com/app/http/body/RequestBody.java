package com.app.http.body;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Haru on 2016/4/12 0012.
 */
public interface RequestBody {
    long getContentLength();

    void setContentType(String contentType);

    String getContentType();

    void writeTo(OutputStream out) throws IOException;
}
