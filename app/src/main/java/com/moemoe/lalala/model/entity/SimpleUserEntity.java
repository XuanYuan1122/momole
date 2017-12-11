package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yi on 2017/11/24.
 */

public class SimpleUserEntity implements Parcelable {
    private String userIcon;
    private String userId;
    private String userName;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<SimpleUserEntity> CREATOR = new Parcelable.Creator<SimpleUserEntity>() {
        @Override
        public SimpleUserEntity createFromParcel(Parcel in) {
            SimpleUserEntity image = new SimpleUserEntity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            image.userIcon = bundle.getString("userIcon");
            image.userId = bundle.getString("userId");
            image.userName = bundle.getString("userName");
            return image;
        }

        @Override
        public SimpleUserEntity[] newArray(int size) {
            return new SimpleUserEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString("userIcon",userIcon);
        bundle.putString("userId",userId);
        bundle.putString("userName",userName);
        dest.writeBundle(bundle);
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
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
