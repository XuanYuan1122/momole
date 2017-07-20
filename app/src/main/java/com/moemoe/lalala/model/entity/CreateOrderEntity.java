package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2017/7/18.
 */

public class CreateOrderEntity{
    @SerializedName("address")
    private AddressEntity address;
    @SerializedName("endTime")
    private String endTime;
    @SerializedName("orderNo")
    private String orderNo;
    @SerializedName("lastRemark")
    private String lastRemark;
    @SerializedName("orderId")
    private String orderId;

    public CreateOrderEntity(){ address = new AddressEntity();}

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getLastRemark() {
        return lastRemark;
    }

    public void setLastRemark(String lastRemark) {
        this.lastRemark = lastRemark;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
