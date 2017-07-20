package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/7/18.
 */

public class PayReqEntity {
    public String address;
    public String channel;
    public String ip;
    public String orderId;
    public String phone;
    public String remark;
    public String userName;

    public PayReqEntity(String address, String channel, String ip, String orderId, String phone, String remark, String userName) {
        this.address = address;
        this.channel = channel;
        this.ip = ip;
        this.orderId = orderId;
        this.phone = phone;
        this.remark = remark;
        this.userName = userName;
    }
}
