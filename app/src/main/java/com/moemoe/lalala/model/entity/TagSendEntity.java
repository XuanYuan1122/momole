package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2016/11/10.
 */

public class TagSendEntity {
    @SerializedName("docId")
    private String docId;
    @SerializedName("tag")
    private String tag;
    @SerializedName("type")
    private String type;
    @SerializedName("dustbinId")
    private String dustbinId;

    public TagSendEntity(String docId, String tag) {
        this.docId = docId;
        this.tag = tag;
    }

    public TagSendEntity(String tag, String type, String dustbinId) {
        this.tag = tag;
        this.type = type;
        this.dustbinId = dustbinId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDustbinId() {
        return dustbinId;
    }

    public void setDustbinId(String dustbinId) {
        this.dustbinId = dustbinId;
    }
}
