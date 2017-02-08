package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2017/1/13.
 */

public class BuildEntity {
    @SerializedName("version")
    private int version;
    @SerializedName("path")
    private String path;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
