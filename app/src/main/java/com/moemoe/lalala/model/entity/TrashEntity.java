package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yi on 2016/12/6.
 */

public class TrashEntity implements Parcelable {
    @SerializedName("dustbinId")
    private String dustbinId; // 小纸条ID
    @SerializedName("title")
    private String title; // 标题
    @SerializedName("content")
    private String content; // 内容
    @SerializedName("img")
    private Image image;
    @SerializedName("fun")
    private int fun; // 滑稽数
    @SerializedName("shit")
    private int shit; // 吃屎数
    @SerializedName("tags")
    private ArrayList<DocTagEntity> tags; // 标签信息
    @SerializedName("mark")
    private boolean mark; // 是否标记
    @SerializedName("timestamp")
    private int timestamp;

    public String getDustbinId() {
        return dustbinId;
    }

    public void setDustbinId(String dustbinId) {
        this.dustbinId = dustbinId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFun() {
        return fun;
    }

    public void setFun(int fun) {
        this.fun = fun;
    }

    public int getShit() {
        return shit;
    }

    public void setShit(int shit) {
        this.shit = shit;
    }

    public ArrayList<DocTagEntity> getTags() {
        return tags;
    }

    public void setTags(ArrayList<DocTagEntity> tags) {
        this.tags = tags;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<TrashEntity> CREATOR = new Parcelable.Creator<TrashEntity>() {
        @Override
        public TrashEntity createFromParcel(Parcel in) {
            TrashEntity entity = new TrashEntity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            entity.dustbinId = bundle.getString("dustbinId");
            entity.title = bundle.getString("title");
            entity.content = bundle.getString("content");
            entity.image = bundle.getParcelable("image");
            entity.fun = bundle.getInt("fun");
            entity.shit = bundle.getInt("shit");
            entity.mark = bundle.getBoolean("mark");
            return entity;
        }

        @Override
        public TrashEntity[] newArray(int size) {
            return new TrashEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("dustbinId",dustbinId);
        bundle.putString("title",title);
        bundle.putString("content", content);
        bundle.putParcelable("image",image);
        bundle.putInt("fun",fun);
        bundle.putInt("shit",shit);
        bundle.putBoolean("mark",mark);
        parcel.writeBundle(bundle);
    }
}
