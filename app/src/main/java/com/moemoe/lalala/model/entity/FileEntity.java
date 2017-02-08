package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2017/1/19.
 */

public class FileEntity implements Parcelable{
    @SerializedName("attr")
    private JsonObject attr;
    @SerializedName("fileName")
    private String fileName;
    @SerializedName("path")
    private String path;
    @SerializedName("type")
    private String type;
    @SerializedName("userName")
    private String userName;
    @SerializedName("fileId")
    private String fileId;

    public JsonObject getAttr() {
        return attr;
    }

    public void setAttr(JsonObject attr) {
        this.attr = attr;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public static final Parcelable.Creator<FileEntity> CREATOR = new Parcelable.Creator<FileEntity>() {
        @Override
        public FileEntity createFromParcel(Parcel in) {
            FileEntity entity = new FileEntity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            entity.attr = new Gson().fromJson(bundle.getString("attr"),JsonObject.class);
            entity.fileName = bundle.getString("fileName");
            entity.path = bundle.getString("path");
            entity.type = bundle.getString("type");
            entity.userName = bundle.getString("userName");
            entity.fileId = bundle.getString("fileId");
            return entity;
        }

        @Override
        public FileEntity[] newArray(int size) {
            return new FileEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString("attr", attr.toString());
        bundle.putString("fileName", fileName);
        bundle.putString("path",path);
        bundle.putString("type",type);
        bundle.putString("userName",userName);
        bundle.putString("fileId",fileId);
        dest.writeBundle(bundle);
    }
}
