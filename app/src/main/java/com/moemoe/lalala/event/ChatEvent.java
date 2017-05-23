package com.moemoe.lalala.event;

import com.moemoe.lalala.model.entity.ChatContentDbEntity;

/**
 * Created by yi on 2017/3/13.
 */

public class ChatEvent {
    private ChatContentDbEntity entity;

    public ChatEvent(ChatContentDbEntity entity) {
        this.entity = entity;
    }

    public ChatContentDbEntity getEntity() {
        return entity;
    }

    public void setEntity(ChatContentDbEntity entity) {
        this.entity = entity;
    }
}
