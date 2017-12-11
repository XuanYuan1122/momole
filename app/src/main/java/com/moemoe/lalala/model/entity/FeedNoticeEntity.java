package com.moemoe.lalala.model.entity;

import com.google.gson.JsonObject;

/**
 * Created by yi on 2017/11/30.
 */

public class FeedNoticeEntity {

    private String createTime;
    private String id;
    private String notifyType;//BAG ARTICLE DYNAMIC SYSTEM STORY
    private JsonObject targetObj;
    private long timestamp;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public JsonObject getTargetObj() {
        return targetObj;
    }

    public void setTargetObj(JsonObject targetObj) {
        this.targetObj = targetObj;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
