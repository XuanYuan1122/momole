package com.moemoe.lalala.event;

/**
 * Created by yi on 2017/5/17.
 */

public class AtUserEvent {
    private String userId;
    private String userName;

    public AtUserEvent(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
