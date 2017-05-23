package com.moemoe.lalala.event;

/**
 * Created by yi on 2017/5/16.
 */

public class RichImgRemoveEvent {
    private String path;

    public RichImgRemoveEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
