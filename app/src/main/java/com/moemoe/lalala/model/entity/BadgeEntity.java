package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2017/1/10.
 */

public class BadgeEntity implements Parcelable{
    private boolean buy;
    private int coin;
    private String color;
    private String desc;
    private boolean have;
    private String id;
    private String img;
    private String name;
    private int rank;
    private String title;

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isHave() {
        return have;
    }

    public void setHave(boolean have) {
        this.have = have;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<BadgeEntity> CREATOR = new Parcelable.Creator<BadgeEntity>() {
        @Override
        public BadgeEntity createFromParcel(Parcel in) {
            BadgeEntity entity = new BadgeEntity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            entity.buy = bundle.getBoolean("buy");
            entity.coin = bundle.getInt("coin");
            entity.color = bundle.getString("color");
            entity.desc = bundle.getString("desc");
            entity.have = bundle.getBoolean("have");
            entity.id = bundle.getString("id");
            entity.img = bundle.getString("img");
            entity.name = bundle.getString("name");
            entity.rank = bundle.getInt("rank");
            entity.title = bundle.getString("title");
            return entity;
        }

        @Override
        public BadgeEntity[] newArray(int size) {
            return new BadgeEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("buy",buy);
        bundle.putInt("coin",coin);
        bundle.putString("color", color);
        bundle.putString("desc", desc);
        bundle.putBoolean("have",have);
        bundle.putString("id",id);
        bundle.putString("img",img);
        bundle.putString("name",name);
        bundle.putInt("rank",rank);
        bundle.putString("title",title);
        parcel.writeBundle(bundle);
    }
}
