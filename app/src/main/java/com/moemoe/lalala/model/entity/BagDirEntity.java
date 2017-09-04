package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2017/1/18.
 */

public class BagDirEntity implements Parcelable{
    @SerializedName("buy")
    private boolean buy;
    @SerializedName("coin")
    private Integer coin;
    @SerializedName("cover")
    private String cover;
    @SerializedName("folderId")
    private String folderId;
    @SerializedName("icon")
    private String icon;
    @SerializedName("name")
    private String name;
    @SerializedName("number")
    private Integer number;
    @SerializedName("updateTime")
    private String updateTime;
    @SerializedName("bagName")
    private String bagName;
    @SerializedName("userId")
    private String userId;
    @SerializedName("size")
    private Long size;
    @SerializedName("userName")
    private String userName;
    @SerializedName("buyUserNum")
    private Integer buyUserNum;
    @SerializedName("readType")
    private String readType;//IMAGE TEXT ""
    @SerializedName("follow")
    private boolean follow;
    @SerializedName("folderType")
    private String folderType;

    private transient boolean isSelect;

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBagName() {
        return bagName;
    }

    public void setBagName(String bagName) {
        this.bagName = bagName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Integer getBuyUserNum() {
        return buyUserNum;
    }

    public void setBuyUserNum(Integer buyUserNum) {
        this.buyUserNum = buyUserNum;
    }

    public Integer getCoin() {
        return coin;
    }

    public Integer getNumber() {
        return number;
    }

    public Long getSize() {
        return size;
    }

    public String getReadType() {
        return readType;
    }

    public void setReadType(String readType) {
        this.readType = readType;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public String getFolderType() {
        return folderType;
    }

    public void setFolderType(String folderType) {
        this.folderType = folderType;
    }

    public static final Parcelable.Creator<BagDirEntity> CREATOR = new Parcelable.Creator<BagDirEntity>() {
        @Override
        public BagDirEntity createFromParcel(Parcel in) {
            BagDirEntity entity = new BagDirEntity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            entity.buy = bundle.getBoolean("buy");
            entity.isSelect = bundle.getBoolean("isSelect");
            entity.coin = bundle.getInt("coin");
            entity.cover = bundle.getString("cover");
            entity.folderId = bundle.getString("folderId");
            entity.name = bundle.getString("name");
            entity.number = bundle.getInt("number");
            entity.updateTime = bundle.getString("updateTime");
            entity.icon = bundle.getString("icon");
            entity.bagName = bundle.getString("bagName");
            entity.userId = bundle.getString("userId");
            entity.size = bundle.getLong("size");
            entity.userName = bundle.getString("userName");
            entity.buyUserNum = bundle.getInt("buyUserNum");
            entity.readType = bundle.getString("readType");
            entity.folderType = bundle.getString("folderType");
            entity.follow = bundle.getBoolean("follow");
            return entity;
        }

        @Override
        public BagDirEntity[] newArray(int size) {
            return new BagDirEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("buy",buy);
        bundle.putBoolean("isSelect",isSelect);
        bundle.putInt("coin",coin);
        bundle.putString("cover", cover);
        bundle.putString("folderId", folderId);
        bundle.putString("name",name);
        bundle.putInt("number",number);
        bundle.putString("updateTime",updateTime);
        bundle.putString("icon",icon);
        bundle.putString("bagName",bagName);
        bundle.putString("userId",userId);
        bundle.putLong("size",size);
        bundle.putString("userName",userName);
        bundle.putInt("buyUserNum",buyUserNum);
        bundle.putString("readType",readType);
        bundle.putString("folderType",folderType);
        bundle.putBoolean("follow",follow);
        parcel.writeBundle(bundle);
    }
}
