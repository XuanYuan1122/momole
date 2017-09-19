package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/9/12.
 */

public class DeskMateEntity {

    private String clothesId;
    private String clothesName;
    private String roleName;
    private String roleOf;
    private boolean deskmate;
    private int likes;

    public String getClothesId() {
        return clothesId;
    }

    public void setClothesId(String clothesId) {
        this.clothesId = clothesId;
    }

    public String getClothesName() {
        return clothesName;
    }

    public void setClothesName(String clothesName) {
        this.clothesName = clothesName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleOf() {
        return roleOf;
    }

    public void setRoleOf(String roleOf) {
        this.roleOf = roleOf;
    }

    public boolean isDeskmate() {
        return deskmate;
    }

    public void setDeskmate(boolean deskmate) {
        this.deskmate = deskmate;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
