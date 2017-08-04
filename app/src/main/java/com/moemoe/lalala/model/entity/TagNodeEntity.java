package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public class TagNodeEntity {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("icon")
    private Image icon;
    @SerializedName("bg")
    private Image bg;
    @SerializedName("texts")
    private ArrayList<String> texts;
    @SerializedName("canMark")
    private boolean canMark;
    @SerializedName("canDoc")
    private boolean canDoc;
    @SerializedName("docNum")
    private int docNum;
    @SerializedName("commentNum")
    private int commentNum;
    @SerializedName("follow")
    private boolean follow;
    @SerializedName("follower")
    private int follower;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getType() {
//        return index;
//    }
//
//    public void setType(String index) {
//        this.index = index;
//    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    public Image getBg() {
        return bg;
    }

    public void setBg(Image bg) {
        this.bg = bg;
    }

    public ArrayList<String> getTexts() {
        return texts;
    }

    public void setTexts(ArrayList<String> texts) {
        this.texts = texts;
    }

    public boolean isCanMark() {
        return canMark;
    }

    public void setCanMark(boolean canMark) {
        this.canMark = canMark;
    }

    public boolean isCanDoc() {
        return canDoc;
    }

    public void setCanDoc(boolean canDoc) {
        this.canDoc = canDoc;
    }

    public int getDocNum() {
        return docNum;
    }

    public void setDocNum(int docNum) {
        this.docNum = docNum;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }
}
