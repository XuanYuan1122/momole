package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2018/1/22.
 */

public class StreamFileEntity implements Parcelable {
    private String id;
    private String path; // 七牛路径
    private String type; // 文件类型 movie music
    private JsonObject attr; // 文件相关属性  图片为:{w:10,h:10},音乐为:{length:200}
    private String updateTime; // 修改时间
    private String fileName; // 文件名称
    private int coin;//节操
    private String summary;//简介  概要
    private String timestamp;//时长
    private int playNum;//播放数
    private int barrageNum;//弹幕数
    private ArrayList<UserFollowTagEntity> texts;
    private String cover;
    private int state;//状态[0:待审核，1：审核通过，-1审核失败]
    private String userId;
    private String userName;
    private String icon;

    private boolean select;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StreamFileEntity> CREATOR = new Creator<StreamFileEntity>() {
        @Override
        public StreamFileEntity createFromParcel(Parcel parcel) {
            StreamFileEntity info = new StreamFileEntity();
            Bundle bundle = parcel.readBundle(getClass().getClassLoader());
            info.id = bundle.getString("id");
            info.path = bundle.getString("path");
            info.type = bundle.getString("type");
            info.attr = new Gson().fromJson(bundle.getString("attr"),JsonObject.class);
            info.updateTime = bundle.getString("updateTime");
            info.fileName = bundle.getString("fileName");
            info.coin = bundle.getInt("coin");
            info.summary = bundle.getString("summary");
            info.timestamp = bundle.getString("timestamp");
            info.playNum = bundle.getInt("playNum");
            info.barrageNum = bundle.getInt("barrageNum");
            info.texts = bundle.getParcelableArrayList("texts");
            info.cover = bundle.getString("cover");
            info.userId = bundle.getString("userId");
            info.userName = bundle.getString("userName");
            info.icon = bundle.getString("icon");
            info.state = bundle.getInt("state");
            return info;
        }

        @Override
        public StreamFileEntity[] newArray(int i) {
            return new StreamFileEntity[0];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("path",path);
        bundle.putString("type",type);
        bundle.putString("attr",attr.toString());
        bundle.putString("updateTime",updateTime);
        bundle.putString("fileName",fileName);
        bundle.putInt("coin",coin);
        bundle.putString("summary",summary);
        bundle.putString("timestamp",timestamp);
        bundle.putInt("playNum",playNum);
        bundle.putInt("barrageNum",barrageNum);
        bundle.putParcelableArrayList("texts",texts);
        bundle.putString("cover",cover);
        bundle.putString("userId",userId);
        bundle.putString("userName",userName);
        bundle.putString("icon",icon);
        bundle.putInt("state",state);
        parcel.writeBundle(bundle);
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonObject getAttr() {
        return attr;
    }

    public void setAttr(JsonObject attr) {
        this.attr = attr;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getPlayNum() {
        return playNum;
    }

    public void setPlayNum(int playNum) {
        this.playNum = playNum;
    }

    public int getBarrageNum() {
        return barrageNum;
    }

    public void setBarrageNum(int barrageNum) {
        this.barrageNum = barrageNum;
    }

    public ArrayList<UserFollowTagEntity> getTexts() {
        return texts;
    }

    public void setTexts(ArrayList<UserFollowTagEntity> texts) {
        this.texts = texts;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
