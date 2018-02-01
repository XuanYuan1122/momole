package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * Created by yi on 2018/1/29.
 */

public class VideoInfo implements Parcelable {
    private int id = 0;
    private String path = null;
    private String name = null;
    private String resolution = null;// 分辨率
    private long size = 0;
    private long date = 0;
    private long duration = 0;

    public VideoInfo(){

    }

    public VideoInfo(int id, String path, String name, String resolution, long size, long date, long duration) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.resolution = resolution;
        this.size = size;
        this.date = date;
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<VideoInfo> CREATOR = new Parcelable.Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel parcel) {
            VideoInfo info = new VideoInfo();
            Bundle bundle = parcel.readBundle(getClass().getClassLoader());
            info.id = bundle.getInt("id");
            info.size = bundle.getLong("size");
            info.date = bundle.getLong("date");
            info.duration = bundle.getLong("duration");
            info.path = bundle.getString("path");
            info.name = bundle.getString("name");
            info.resolution = bundle.getString("resolution");
            return info;
        }

        @Override
        public VideoInfo[] newArray(int i) {
            return new VideoInfo[0];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("id",id);
        bundle.putLong("size",size);
        bundle.putLong("date",date);
        bundle.putLong("duration",duration);
        bundle.putString("path",path);
        bundle.putString("name",name);
        bundle.putString("resolution",resolution);
        parcel.writeBundle(bundle);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
