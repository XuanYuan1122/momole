package com.moemoe.lalala.event;

/**
 * Created by yi on 2017/5/17.
 */

public class EventDoneEvent {
    private String role;
    private String type;//map mobile

    public EventDoneEvent(String type,String role) {
        this.role = role;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
