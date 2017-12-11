package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/11/24.
 */

public class ShareLive2dEntity {

    private boolean have;
    private Integer maxNum;
    private Integer nowNum;
    private String roleOf;

    public Integer getNowNum() {
        return nowNum;
    }

    public void setNowNum(Integer nowNum) {
        this.nowNum = nowNum;
    }

    public boolean isHave() {
        return have;
    }

    public void setHave(boolean have) {
        this.have = have;
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    public String getRoleOf() {
        return roleOf;
    }

    public void setRoleOf(String roleOf) {
        this.roleOf = roleOf;
    }
}
