package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/12/14.
 */

public class FeedRecommendUserEntity {
    /**
     bg (string, optional): 背景图 ,
     mark (string, optional): 角标 ,
     signature (string, optional): 用户签名 ,
     userIcon (string, optional): 用户头像 ,
     userId (string, optional): 用户ID ,
     userName (string, optional): 用户名称
     */
    private String bg;
    private String mark;
    private String signature;
    private String userIcon;
    private String userId;
    private String userName;
    private boolean follow;

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
