package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by yi on 2017/6/7.
 */

public class RichDocListEntity implements Parcelable{

    private ArrayList<RichEntity> list;
    private ArrayList<RichEntity> hideList;
    private ArrayList<DocTagEntity> tags;
    private String docId;
    private String musicPath;
    private String musicTitle;
    private String folderId;
    private Image cover;
    private int time;

    public RichDocListEntity(){
        list = new ArrayList<>();
        hideList = new ArrayList<>();
        tags = new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RichDocListEntity> CREATOR = new Creator<RichDocListEntity>() {
        @Override
        public RichDocListEntity createFromParcel(Parcel parcel) {
            RichDocListEntity info = new RichDocListEntity();
            Bundle bundle = parcel.readBundle();
            info.list = bundle.getParcelableArrayList("list");
            info.hideList = bundle.getParcelableArrayList("hideList");
            info.tags = bundle.getParcelableArrayList("tags");
            info.docId = bundle.getString("docId");
            info.musicPath = bundle.getString("musicPath");
            info.musicTitle = bundle.getString("musicTitle");
            info.folderId = bundle.getString("folderId");
            info.time = bundle.getInt("time");
            info.cover = bundle.getParcelable("cover");
            return info;
        }

        @Override
        public RichDocListEntity[] newArray(int i) {
            return new RichDocListEntity[0];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list",list);
        bundle.putParcelableArrayList("hideList",hideList);
        bundle.putParcelableArrayList("tags",tags);
        bundle.putString("docId",docId);
        bundle.putString("musicPath",musicPath);
        bundle.putString("musicTitle",musicTitle);
        bundle.putString("folderId",folderId);
        bundle.putInt("time",time);
        bundle.putParcelable("cover",cover);
        dest.writeBundle(bundle);
    }

    public ArrayList<RichEntity> getList() {
        return list;
    }

    public void setList(ArrayList<RichEntity> list) {
        this.list = list;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public ArrayList<RichEntity> getHideList() {
        return hideList;
    }

    public void setHideList(ArrayList<RichEntity> hideList) {
        this.hideList = hideList;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public Image getCover() {
        return cover;
    }

    public void setCover(Image cover) {
        this.cover = cover;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public ArrayList<DocTagEntity> getTags() {
        return tags;
    }

    public void setTags(ArrayList<DocTagEntity> tags) {
        this.tags = tags;
    }
}
