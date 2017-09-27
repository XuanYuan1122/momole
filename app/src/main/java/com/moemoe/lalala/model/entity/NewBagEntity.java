package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/8/18.
 */

public class NewBagEntity {

    private String bagName;
    private String bg;
    private long maxSize;
    private long useSize;
    private String userId;
    private String userName;

    public String getBagName() {
        return bagName;
    }

    public void setBagName(String bagName) {
        this.bagName = bagName;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
