package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2017/1/18.
 */

public class UploadResultEntity {
    @SerializedName("attr")
    private String attr;
    @SerializedName("md5")
    private String md5;
    @SerializedName("path")
    private String path;
    @SerializedName("save")
    private boolean save;
    @SerializedName("size")
    private long size;
    @SerializedName("type")
    private String type;
    @SerializedName("uploadToken")
    private String uploadToken;

    private String fileName;
    private transient String filePath;
    private transient int musicTime;

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUploadToken() {
        return uploadToken;
    }

    public void setUploadToken(String uploadToken) {
        this.uploadToken = uploadToken;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getMusicTime() {
        return musicTime;
    }

    public void setMusicTime(int musicTime) {
        this.musicTime = musicTime;
    }
}
