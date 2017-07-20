package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 拉黑用户列表
 * Created by yi on 2017/7/6.
 */

public class RejectEntity {
    @SerializedName("headPath")
    private String headPath;
    @SerializedName("sex")
    private String sex;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userName")
    private String userName;

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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
