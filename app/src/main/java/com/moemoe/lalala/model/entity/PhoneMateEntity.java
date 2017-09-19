package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2017/9/12.
 */

public class PhoneMateEntity {
    private String desc;
    private String role;
    private String roleName;
    private ArrayList<RoleLike> roleLikes;

    public PhoneMateEntity(){
        roleLikes = new ArrayList<>();
    }

    public class RoleLike{
        private String levelColor;
        private String levelName;
        private int end;
        private int start;

        public String getLevelColor() {
            return levelColor;
        }

        public void setLevelColor(String levelColor) {
            this.levelColor = levelColor;
        }

        public String getLevelName() {
            return levelName;
        }

        public void setLevelName(String levelName) {
            this.levelName = levelName;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public ArrayList<RoleLike> getRoleLikes() {
        return roleLikes;
    }

    public void setRoleLikes(ArrayList<RoleLike> roleLikes) {
        this.roleLikes = roleLikes;
    }
}
