package com.moemoe.lalala.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class Image implements Parcelable,Serializable{

    public final static String KEY_REAL_PATH = "real_path";
    public final static String KEY_LOCAL_PATH = "local_path";
    public final static String KEY_PATH = "path";
    public final static String KEY_W = "w";
    public final static String KEY_H = "h";

    public String real_path;
    public String local_path;
    public String path;
    public int w;
    public int h;

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            Image image = new Image();
            Bundle bundle = new Bundle();
            bundle = in.readBundle();
            image.local_path = bundle.getString(KEY_LOCAL_PATH);
            image.path = bundle.getString(KEY_PATH);
            image.w = bundle.getInt(KEY_W);
            image.h = bundle.getInt(KEY_H);
            image.real_path = bundle.getString(KEY_REAL_PATH);
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
        dest.writeBundle(bundle);
    }
}
