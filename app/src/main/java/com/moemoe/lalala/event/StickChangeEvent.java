package com.moemoe.lalala.event;

/**
 * Created by yi on 2017/12/4.
 */

public class StickChangeEvent {
    private String roleId;
    private int position;

    public StickChangeEvent(String roleId, int position) {
        this.roleId = roleId;
        this.position = position;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
