package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2016/11/10.
 */

public class LoginEntity {

    public String mobile;

    public String password;

    public String deviceId;

    public LoginEntity(String mobile, String password, String deviceId) {
        this.mobile = mobile;
        this.password = password;
        this.deviceId = deviceId;
    }
}
