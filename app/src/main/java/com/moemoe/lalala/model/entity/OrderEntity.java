package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 *
 * Created by yi on 2017/7/14.
 */

public class OrderEntity implements Parcelable{
    private String endTime;
    private String orderNo;
    private AddressEntity address;
    private String icon;
    private String desc;
    private String orderType;
    private int coin;
    private String orderId;
    private String productId;
    private String productName;
    private String remark;
    private int rmb;
    private int status;
    private int buyNum;

    private String lastRemark;

    public OrderEntity(){
        address = new AddressEntity();
    }

    public int getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(int buyNum) {
        this.buyNum = buyNum;
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

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String name) {
        this.productName = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getRmb() {
        return rmb;
    }

    public void setRmb(int rmb) {
        this.rmb = rmb;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLastRemark() {
        return lastRemark;
    }

    public void setLastRemark(String lastRemark) {
        this.lastRemark = lastRemark;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderEntity> CREATOR = new Creator<OrderEntity>() {
        @Override
        public OrderEntity createFromParcel(Parcel parcel) {
            OrderEntity info = new OrderEntity();
            Bundle bundle = parcel.readBundle(getClass().getClassLoader());
            info.endTime = bundle.getString("endTime");
            info.orderNo = bundle.getString("orderNo");
            info.icon = bundle.getString("icon");
            info.productName = bundle.getString("productName");
            info.desc = bundle.getString("desc");
            info.orderId = bundle.getString("orderId");
            info.orderType = bundle.getString("orderType");
            info.productId = bundle.getString("productId");
            info.remark = bundle.getString("remark");
            info.rmb = bundle.getInt("rmb");
            info.address = bundle.getParcelable("address");
            info.coin = bundle.getInt("coin");
            info.status = bundle.getInt("status");
            info.lastRemark = bundle.getString("lastRemark");
            info.buyNum = bundle.getInt("buyNum");
            return info;
        }

        @Override
        public OrderEntity[] newArray(int i) {
            return new OrderEntity[0];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("endTime",endTime);
        bundle.putString("orderNo",orderNo);
        bundle.putString("icon",icon);
        bundle.putString("productName",productName);
        bundle.putString("desc",desc);
        bundle.putString("orderId",orderId);
        bundle.putString("orderType",orderType);
        bundle.putString("productId",productId);
        bundle.putString("remark",remark);
        bundle.putString("lastRemark",lastRemark);
        bundle.putInt("rmb",rmb);
        bundle.putParcelable("address",address);
        bundle.putInt("coin",coin);
        bundle.putInt("status",status);
        bundle.putInt("buyNum",buyNum);
        parcel.writeBundle(bundle);
    }
}
