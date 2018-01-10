package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * 转发动态
 * Created by yi on 2017/9/20.
 */

public class RetweetEntity {
    private String createUserName; //创建人
    private String createUserId;//创建人ID
    private String content;
    private String oldDynamicId;
    private ArrayList<Image> images;
    private String createTime;
    private int likes;
    private int comments;
    private int rtNum;
    private int coins;
    private int surplus;
    private int users;
    private String createUserHead;

    public String getCreateUserHead() {
        return createUserHead;
    }

    public void setCreateUserHead(String createUserHead) {
        this.createUserHead = createUserHead;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getSurplus() {
        return surplus;
    }

    public void setSurplus(int surplus) {
        this.surplus = surplus;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getRtNum() {
        return rtNum;
    }

    public void setRtNum(int rtNum) {
        this.rtNum = rtNum;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public String getOldDynamicId() {
        return oldDynamicId;
    }

    public void setOldDynamicId(String oldDynamicId) {
        this.oldDynamicId = oldDynamicId;
    }
}
