package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2016/11/10.
 */

public class TagLikeEntity {
    @SerializedName("docId")
    private String docId;
    @SerializedName("tagId")
    private String tagId;
    @SerializedName("dustbinId")
    private String dustbinId;
    @SerializedName("type")
    private String type;

    public TagLikeEntity(String docId, String tag) {
        this.docId = docId;
        this.tagId = tag;
    }

    public TagLikeEntity(String tagId, String dustbinId, String type) {
        this.tagId = tagId;
        this.dustbinId = dustbinId;
        this.type = type;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getDustbinId() {
        return dustbinId;
    }

    public void setDustbinId(String dustbinId) {
        this.dustbinId = dustbinId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
