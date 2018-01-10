package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.moemoe.lalala.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by yi on 2017/6/26.
 */

public class CoinShopEntity implements Parcelable{
    private int coin;
    private String desc;
    private int freeze;
    private String icon;
    private String id;
    private ArrayList<Image> images;
    private String orderType;
    private String productName;
    private int rmb;
    private int stock;
    private String stockDesc;
    private int buyLimit;

    public CoinShopEntity() {
        images = new ArrayList<>();
    }

    public int getBuyLimit() {
        return buyLimit;
    }

    public void setBuyLimit(int buyLimit) {
        this.buyLimit = buyLimit;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getFreeze() {
        return freeze;
    }

    public void setFreeze(int freeze) {
        this.freeze = freeze;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getRmb() {
        return rmb;
    }

    public void setRmb(int rmb) {
        this.rmb = rmb;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getStockDesc() {
        return stockDesc;
    }

    public void setStockDesc(String stockDesc) {
        this.stockDesc = stockDesc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CoinShopEntity> CREATOR = new Creator<CoinShopEntity>() {
        @Override
        public CoinShopEntity createFromParcel(Parcel parcel) {
            CoinShopEntity info = new CoinShopEntity();
            Bundle bundle = parcel.readBundle(getClass().getClassLoader());
            info.coin = bundle.getInt("coin");
            info.freeze = bundle.getInt("freeze");
            info.desc = bundle.getString("desc");
            info.icon = bundle.getString("icon");
            info.id = bundle.getString("id");
            info.orderType = bundle.getString("orderType");
            info.productName = bundle.getString("productName");
            info.images = bundle.getParcelableArrayList("images");
            info.rmb = bundle.getInt("rmb");
            info.stock = bundle.getInt("stock");
            info.stockDesc = bundle.getString("stockDesc");
            return info;
        }

        @Override
        public CoinShopEntity[] newArray(int i) {
            return new CoinShopEntity[0];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("coin",coin);
        bundle.putInt("freeze",freeze);
        bundle.putString("desc",desc);
        bundle.putString("icon",icon);
        bundle.putString("id",id);
        bundle.putString("orderType",orderType);
        bundle.putString("productName",productName);
        bundle.putParcelableArrayList("images",images);
        bundle.putInt("rmb",rmb);
        bundle.putInt("stock",stock);
        bundle.putString("stockDesc",stockDesc);
        parcel.writeBundle(bundle);
    }
}
