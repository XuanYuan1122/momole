package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 顶部用户信息
 * Created by yi on 2017/9/20.
 */

public class UserTopEntity implements Parcelable {
    private String userId;
    private String userName;
    private String headPath;
    private String sex;
    private String levelColor;
    private int level;
    private BadgeEntity badge;
    private boolean vip;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<UserTopEntity> CREATOR = new Parcelable.Creator<UserTopEntity>() {
        @Override
        public UserTopEntity createFromParcel(Parcel in) {
            UserTopEntity entity = new UserTopEntity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            entity.userId = bundle.getString("userId");
            entity.userName = bundle.getString("userName");
            entity.headPath = bundle.getString("headPath");
            entity.sex = bundle.getString("sex");
            entity.levelColor = bundle.getString("levelColor");
            entity.level = bundle.getInt("level");
            entity.vip = bundle.getBoolean("vip");
            entity.badge = bundle.getParcelable("badge");
            return entity;
        }

        @Override
        public UserTopEntity[] newArray(int size) {
            return new UserTopEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        bundle.putString("userName", userName);
        bundle.putString("headPath",headPath);
        bundle.putString("sex",sex);
        bundle.putString("levelColor",levelColor);
        bundle.putInt("level",level);
        bundle.putBoolean("vip",vip);
        bundle.putParcelable("badge",badge);
        parcel.writeBundle(bundle);
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

    public String getLevelColor() {
        return levelColor;
    }

    public void setLevelColor(String levelColor) {
        this.levelColor = levelColor;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public BadgeEntity getBadge() {
        return badge;
    }

    public void setBadge(BadgeEntity badge) {
        this.badge = badge;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }
}
