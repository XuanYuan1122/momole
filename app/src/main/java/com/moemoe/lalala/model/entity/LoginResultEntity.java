package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2016/11/12.
 */

public class LoginResultEntity {
    @SerializedName("token")
    private String token;
    @SerializedName("userId")
    private String userId;
    @SerializedName("isNew")
    private boolean isNew;
    @SerializedName("coin")
    private int coin;
    @SerializedName("headPath")
    private String headPath;
    @SerializedName("userName")
    private String userName;
    @SerializedName("level")
    private int level;
    @SerializedName("openBag")
    private boolean openBag;

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isOpenBag() {
        return openBag;
    }

    public void setOpenBag(boolean openBag) {
        this.openBag = openBag;
    }
}
