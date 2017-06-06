package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Haru on 2016/5/12 0012.
 */
public class ReplyEntity {

    @SerializedName("commentId")
    private String commentId;
    @SerializedName("content")
    private String content;
    @SerializedName("createTime")
    private String createTime;
    @SerializedName("fromIcon")
    private Image fromIcon;
    @SerializedName("fromName")
    private String fromName;
    @SerializedName("schema")
    private String schema;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Image getFromIcon() {
        return fromIcon;
    }

    public void setFromIcon(Image fromIcon) {
        this.fromIcon = fromIcon;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
