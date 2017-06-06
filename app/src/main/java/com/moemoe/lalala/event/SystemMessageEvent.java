package com.moemoe.lalala.event;

/**
 * Created by yi on 2017/3/13.
 */

public class SystemMessageEvent {
    private String type;

    public SystemMessageEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
