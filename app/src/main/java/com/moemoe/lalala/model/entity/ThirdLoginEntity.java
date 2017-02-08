package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2016/11/10.
 */

public class ThirdLoginEntity {

    public String nickname;
    public String openId;
    public String platform;
    public String deviceId;

    public ThirdLoginEntity(String nickname, String openId, String platform, String deviceId) {
        this.nickname = nickname;
        this.openId = openId;
        this.platform = platform;
        this.deviceId = deviceId;
    }
}
