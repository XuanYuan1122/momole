package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 *
 * Created by yi on 2017/8/20.
 */

public class CommonFileEntity implements Parcelable{
    private String path; // 文件路径
    private JsonObject attr; // 文件属性
    private String type; // 文件类型//txt image music
    private String fileName; // 文件名称
    private String userName; // 用户名
    private String fileId; // 文件ID
    private boolean select;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommonFileEntity> CREATOR = new Creator<CommonFileEntity>() {
        @Override
        public CommonFileEntity createFromParcel(Parcel parcel) {
            CommonFileEntity info = new CommonFileEntity();
            Bundle bundle = parcel.readBundle(getClass().getClassLoader());
            info.path = bundle.getString("path");
            info.attr = new Gson().fromJson(bundle.getString("attr"),JsonObject.class);
            info.type = bundle.getString("type");
            info.fileName = bundle.getString("fileName");
            info.userName = bundle.getString("userName");
            info.fileId = bundle.getString("fileId");
            return info;
        }

        @Override
        public CommonFileEntity[] newArray(int i) {
            return new CommonFileEntity[0];
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

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
