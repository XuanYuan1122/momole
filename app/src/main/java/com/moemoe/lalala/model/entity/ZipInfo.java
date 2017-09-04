package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yi on 2017/8/21.
 */

public class ZipInfo implements Parcelable {

    private String id;
    private String cover;
    private String shortInfo;
    private String title;
    private String path;
    private boolean isFromSD;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getShortInfo() {
        return shortInfo;
    }

    public void setShortInfo(String shortInfo) {
        this.shortInfo = shortInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFromSD() {
        return isFromSD;
    }

    public void setFromSD(boolean fromSD) {
        isFromSD = fromSD;
    }

    public static final Parcelable.Creator<ZipInfo> CREATOR = new Parcelable.Creator<ZipInfo>() {
        @Override
        public ZipInfo createFromParcel(Parcel in) {
            ZipInfo entity = new ZipInfo();
            Bundle bundle = in.readBundle(getClass().getClassLoader());
            entity.id = bundle.getString("id");
            entity.cover = bundle.getString("cover");
            entity.shortInfo = bundle.getString("shortInfo");
            entity.title = bundle.getString("title");
            entity.path = bundle.getString("path");
            entity.isFromSD = bundle.getBoolean("isFromSD");
            return entity;
        }

        @Override
        public ZipInfo[] newArray(int size) {
            return new ZipInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("cover",cover);
        bundle.putString("shortInfo",shortInfo);
        bundle.putString("title",title);
        bundle.putString("path",path);
        bundle.putBoolean("isFromSD",isFromSD);
        dest.writeBundle(bundle);
    }
}
