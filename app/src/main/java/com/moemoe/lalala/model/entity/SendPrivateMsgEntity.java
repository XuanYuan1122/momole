package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/3/14.
 */

public class SendPrivateMsgEntity {
    public String content;
    public String talkId;
    public String type;

    public SendPrivateMsgEntity(String content, String talkId, String type) {
        this.content = content;
        this.talkId = talkId;
        this.type = type;
    }
}
