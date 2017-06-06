package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2017/3/14.
 */

public class CreatePrivateMsgEntity {
    @SerializedName("ignore")
    private boolean ignore;
    @SerializedName("talkId")
    private String talkId;

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public String getTalkId() {
        return talkId;
    }

    public void setTalkId(String talkId) {
        this.talkId = talkId;
    }
}
