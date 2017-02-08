package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2016/12/15.
 */

public class PersonDocEntity {
    @SerializedName("comments")
    private int comments;
    @SerializedName("createTime")
    private String createTime;
    @SerializedName("desc")
    private String desc;
    @SerializedName("docId")
    private String docId;
    @SerializedName("image")
    private String image;
    @SerializedName("likes")
    private int likes;
    @SerializedName("schema")
    private String schema;
    @SerializedName("title")
    private String title;

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
