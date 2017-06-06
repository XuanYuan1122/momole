package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by yi on 2017/3/10.
 */
public class ChatContentEntity {

    @SerializedName("content")
    private String content;
    @SerializedName("contentType")
    private String contentType;
    @SerializedName("createTime")
    private Date createTime;
    @SerializedName("id")
    private String id;
    @SerializedName("talkId")
    private String talkId;
    @SerializedName("userIcon")
    private String userIcon;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userName")
    private String userName;

    public ChatContentEntity(){}

    public ChatContentEntity(ChatContentDbEntity entity) {
        content = entity.getContent();
        contentType = entity.getContentType();
        createTime = entity.getCreateTime();
        id = entity.getId();
        talkId = entity.getTalkId();
        userIcon = entity.getUser().getUserIcon();
        userId = entity.getUser().getUserId();
        userName = entity.getUser().getUserName();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTalkId() {
        return talkId;
    }

    public void setTalkId(String talkId) {
        this.talkId = talkId;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
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

}
