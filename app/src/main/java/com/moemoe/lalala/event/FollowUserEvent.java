package com.moemoe.lalala.event;

/**
 * Created by yi on 2017/12/15.
 */

public class FollowUserEvent {
    private String userId;
    private boolean isFollow;
    private int position;

    public FollowUserEvent(String userId, boolean isFollow, int position) {
        this.userId = userId;
        this.isFollow = isFollow;
        this.position = position;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
