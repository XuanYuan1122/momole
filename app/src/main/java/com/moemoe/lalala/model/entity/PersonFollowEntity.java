package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2016/12/15.
 */

public class PersonFollowEntity {
    @SerializedName("signature")
    private String signature;
    @SerializedName("userIcon")
    private String userIcon;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userLevel")
    private int userLevel;
    @SerializedName("userLevelColor")
    private String userLevelColor;
    @SerializedName("userLevelName")
    private String userLevelName;
    @SerializedName("userName")
    private String userName;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserLevelColor() {
        return userLevelColor;
    }

    public void setUserLevelColor(String userLevelColor) {
        this.userLevelColor = userLevelColor;
    }

    public String getUserLevelName() {
        return userLevelName;
    }

    public void setUserLevelName(String userLevelName) {
        this.userLevelName = userLevelName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
