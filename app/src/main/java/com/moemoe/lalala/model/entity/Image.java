package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by yi on 2016/11/29.
 */

public class Image implements Parcelable,Serializable {
    private final static String KEY_REAL_PATH = "real_path";
    private final static String KEY_LOCAL_PATH = "local_path";
    private final static String KEY_PATH = "path";
    private final static String KEY_W = "w";
    private final static String KEY_H = "h";

    private String real_path;
    private String local_path;
    @SerializedName("path")
    private String path;
    @SerializedName("w")
    private int w;
    @SerializedName("h")
    private int h;
    @SerializedName("size")
    private long size;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public String getReal_path() {
        return real_path;
    }

    public void setReal_path(String real_path) {
        this.real_path = real_path;
    }

    public String getLocal_path() {
        return local_path;
    }

    public void setLocal_path(String local_path) {
        this.local_path = local_path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            Image image = new Image();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            image.local_path = bundle.getString(KEY_LOCAL_PATH);
            image.path = bundle.getString(KEY_PATH);
            image.w = bundle.getInt(KEY_W);
            image.h = bundle.getInt(KEY_H);
            image.real_path = bundle.getString(KEY_REAL_PATH);
            image.size = bundle.getLong("size");
            return image;
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LOCAL_PATH,local_path);
        bundle.putString(KEY_PATH,path);
        bundle.putInt(KEY_W, w);
        bundle.putInt(KEY_H, h);
        bundle.putString(KEY_REAL_PATH,real_path);
        bundle.putLong("size",size);
        dest.writeBundle(bundle);
    }
}
