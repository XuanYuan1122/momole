package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2018/1/15.
 */

public class FeedFollowType1Entity {
    private String id;
    private int position;
    private String type;//WZ MOVIE MUSIC ZH XS
    private String cover;
    private String title;
    private ArrayList<UserFollowTagEntity> tags;
    private String userAvatar;
    private String userName;
    private String userId;
    private String createTime;
    /**
     * json
     * movie{playNum:int,danmuNum:int,stampTime:int,coin:int}
     * music{playNum:int,stampTime:int,coin:int}
     * doc {readNum:int}
     * folder {coin:int,fileNum:int,type:string}
     */
    private String extra;
    private String folderId;

    private boolean select;

    public FeedFollowType1Entity(){
        tags = new ArrayList<>();
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<UserFollowTagEntity> getTags() {
        return tags;
    }

    public void setTags(ArrayList<UserFollowTagEntity> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
