package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2016/11/11.
 */

public class UploadEntity {
    @SerializedName("filePath")
    private String filePath;
    @SerializedName("uploadToken")
    private String uploadToken;

    private String localPath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUploadToken() {
        return uploadToken;
    }

    public void setUploadToken(String uploadToken) {
        this.uploadToken = uploadToken;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
