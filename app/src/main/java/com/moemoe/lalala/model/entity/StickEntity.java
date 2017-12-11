package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2017/12/4.
 */

public class StickEntity {
    private String roleId;
    private String roleName;
    private ArrayList<Stick> sticks;

    public StickEntity(){
        sticks = new ArrayList<>();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public ArrayList<Stick> getSticks() {
        return sticks;
    }

    public void setSticks(ArrayList<Stick> sticks) {
        this.sticks = sticks;
    }

    public static class Stick implements Parcelable{
        private String stickId;
        private boolean belong;
        private int coin;
        private String path;
        private String smallPath;
        private String stickDesc;
        private String stickName;
        private String type;//VIP JC CY FREE

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Parcelable.Creator<Stick> CREATOR = new Parcelable.Creator<Stick>() {
            @Override
            public Stick createFromParcel(Parcel in) {
                Stick entity = new StickEntity.Stick();
                Bundle bundle;
                bundle = in.readBundle(getClass().getClassLoader());
                entity.stickId = bundle.getString("stickId");
                entity.path = bundle.getString("path");
                entity.smallPath = bundle.getString("smallPath");
                entity.stickDesc = bundle.getString("stickDesc");
                entity.stickName = bundle.getString("stickName");
                entity.type = bundle.getString("type");
                entity.coin = bundle.getInt("coin");
                entity.belong = bundle.getBoolean("belong");
                return entity;
            }

            @Override
            public Stick[] newArray(int size) {
                return new Stick[size];
            }
        };

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            Bundle bundle = new Bundle();
            bundle.putString("stickId", stickId);
            bundle.putString("path", path);
            bundle.putString("smallPath", smallPath);
            bundle.putString("stickDesc",stickDesc);
            bundle.putString("stickName",stickName);
            bundle.putString("type",type);
            bundle.putInt("coin",coin);
            bundle.putBoolean("belong",belong);
            parcel.writeBundle(bundle);
        }

        public String getStickId() {
            return stickId;
        }

        public void setStickId(String stickId) {
            this.stickId = stickId;
        }

        public boolean isBelong() {
            return belong;
        }

        public void setBelong(boolean belong) {
            this.belong = belong;
        }

        public int getCoin() {
            return coin;
        }

        public void setCoin(int coin) {
            this.coin = coin;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getSmallPath() {
            return smallPath;
        }

        public void setSmallPath(String smallPath) {
            this.smallPath = smallPath;
        }

        public String getStickDesc() {
            return stickDesc;
        }

        public void setStickDesc(String stickDesc) {
            this.stickDesc = stickDesc;
        }

        public String getStickName() {
            return stickName;
        }

        public void setStickName(String stickName) {
            this.stickName = stickName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
