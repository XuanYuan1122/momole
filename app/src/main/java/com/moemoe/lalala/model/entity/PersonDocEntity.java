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
    @SerializedName("createUserName")
    private String createUserName;
    @SerializedName("docType")
    private String docType;
    @SerializedName("docTypeSchema")
    private String docTypeSchema;
    @SerializedName("createUserId")
    private String createUserId;

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

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocTypeSchema() {
        return docTypeSchema;
    }

    public void setDocTypeSchema(String docTypeSchema) {
        this.docTypeSchema = docTypeSchema;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }
}
