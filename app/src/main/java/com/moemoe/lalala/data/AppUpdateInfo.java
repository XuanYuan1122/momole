package com.moemoe.lalala.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Haru on 2016/5/12 0012.
 */
public class AppUpdateInfo {

    @SerializedName("url")
    private String url;
    @SerializedName("updateStatus")
    private int updateStatus;//0.no 1.update 2.force-update
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(int updateStatus) {
        this.updateStatus = updateStatus;
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
}
