package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * 官方标签
 * Created by yi on 2018/1/22.
 */

public class OfficialTag {

    private String id;
    private String text;
    private ArrayList<OfficialTagSec> tagSec;

    public class OfficialTagSec{
        private String id;
        private String text;
        private ArrayList<UserFollowTagEntity> tagThi;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public ArrayList<UserFollowTagEntity> getTagThi() {
            return tagThi;
        }

        public void setTagThi(ArrayList<UserFollowTagEntity> tagThi) {
            this.tagThi = tagThi;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<OfficialTagSec> getTagSec() {
        return tagSec;
    }

    public void setTagSec(ArrayList<OfficialTagSec> tagSec) {
        this.tagSec = tagSec;
    }
}
