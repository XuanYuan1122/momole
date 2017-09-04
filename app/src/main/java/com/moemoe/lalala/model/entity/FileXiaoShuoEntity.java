package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by yi on 2017/8/22.
 */

public class FileXiaoShuoEntity implements Parcelable {
    private String path; // 文件路径
    private JsonObject attr; // 文件属性
    private String type; // 文件类型
    private String fileName; // 文件名称
    private String userName; // 用户名
    private String fileId; // 文件ID

    private String cover; // 封面
    private String title; // 标题
    private String content; // 简介内容
    private int num;// 字数
    private long coverSize;	//封面大小
    private boolean select;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileXiaoShuoEntity> CREATOR = new Creator<FileXiaoShuoEntity>() {
        @Override
        public FileXiaoShuoEntity createFromParcel(Parcel parcel) {
            FileXiaoShuoEntity info = new FileXiaoShuoEntity();
            Bundle bundle = parcel.readBundle(getClass().getClassLoader());
            info.path = bundle.getString("path");
            info.attr =  new Gson().fromJson(bundle.getString("attr"),JsonObject.class);
            info.type = bundle.getString("type");
            info.fileName = bundle.getString("fileName");
            info.userName = bundle.getString("userName");
            info.fileId = bundle.getString("fileId");
            info.cover = bundle.getString("cover");
            info.title = bundle.getString("title");
            info.content = bundle.getString("content");
            info.num = bundle.getInt("num");
            info.coverSize = bundle.getLong("coverSize");
            return info;
        }

        @Override
        public FileXiaoShuoEntity[] newArray(int i) {
            return new FileXiaoShuoEntity[0];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("path",path);
        bundle.putString("attr",attr.toString());
        bundle.putString("type",type);
        bundle.putString("fileName",fileName);
        bundle.putString("userName",userName);
        bundle.putString("fileId",fileId);
        bundle.putString("cover",cover);
        bundle.putString("title",title);
        bundle.putString("content",content);
        bundle.putInt("num",num);
        bundle.putLong("coverSize",coverSize);
        parcel.writeBundle(bundle);
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public JsonObject getAttr() {
        return attr;
    }

    public void setAttr(JsonObject attr) {
        this.attr = attr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getCoverSize() {
        return coverSize;
    }

    public void setCoverSize(long coverSize) {
        this.coverSize = coverSize;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
