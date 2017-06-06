package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2017/1/18.
 */

public class BagEntity {
    @SerializedName("bg")
    private String bg;
    @SerializedName("name")
    private String name;
    @SerializedName("updateTime")
    private String updateTime;
    @SerializedName("maxSize")
    private long maxSize;
    @SerializedName("useSize")
    private long useSize;

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public long getUseSize() {
        return useSize;
    }

    public void setUseSize(long useSize) {
        this.useSize = useSize;
    }
}
