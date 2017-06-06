package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/4/18 0018.
 */
public class NewCommentEntity {
    @SerializedName("id")
    private String id;
    @SerializedName("content")
    private String content;
    @SerializedName("fromUserId")
    private String fromUserId;
    @SerializedName("fromUserName")
    private String fromUserName;
    @SerializedName("fromUserIcon")
    private Image fromUserIcon;
    @SerializedName("fromUserLevel")
    private int fromUserLevel;
    @SerializedName("fromUserLevelName")
    private String fromUserLevelName;
    @SerializedName("fromUserLevelColor")
    private String fromUserLevelColor;
    @SerializedName("fromUserSex")
    private String fromUserSex;
    @SerializedName("toUserId")
    private String toUserId;
    @SerializedName("toUserName")
    private String toUserName;
    @SerializedName("toUserIcon")
    private  Image toUserIcon;
    @SerializedName("toUserLevel")
    private int toUserLevel;
    @SerializedName("toUserLevelName")
    private String toUserLevelName;
    @SerializedName("toUserLevelColor")
    private String toUserLevelColor;
    @SerializedName("toUserSex")
    private String toUserSex;
    @SerializedName("createTime")
    private String createTime;
    @SerializedName("images")
    private ArrayList<Image> images;
    @SerializedName("deleteFlag")
    private boolean deleteFlag;
    @SerializedName("newDeleteFlag")
    private boolean newDeleteFlag;
    @SerializedName("idx")
    private int idx;
    @SerializedName("badgeList")
    private ArrayList<BadgeEntity> badgeList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public Image getFromUserIcon() {
        return fromUserIcon;
    }

    public void setFromUserIcon(Image fromUserIcon) {
        this.fromUserIcon = fromUserIcon;
    }

    public int getFromUserLevel() {
        return fromUserLevel;
    }

    public void setFromUserLevel(int fromUserLevel) {
        this.fromUserLevel = fromUserLevel;
    }

    public String getFromUserLevelName() {
        return fromUserLevelName;
    }

    public void setFromUserLevelName(String fromUserLevelName) {
        this.fromUserLevelName = fromUserLevelName;
    }

    public String getFromUserLevelColor() {
        return fromUserLevelColor;
    }

    public void setFromUserLevelColor(String fromUserLevelColor) {
        this.fromUserLevelColor = fromUserLevelColor;
    }

    public String getFromUserSex() {
        return fromUserSex;
    }

    public void setFromUserSex(String fromUserSex) {
        this.fromUserSex = fromUserSex;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public Image getToUserIcon() {
        return toUserIcon;
    }

    public void setToUserIcon(Image toUserIcon) {
        this.toUserIcon = toUserIcon;
    }

    public int getToUserLevel() {
        return toUserLevel;
    }

    public void setToUserLevel(int toUserLevel) {
        this.toUserLevel = toUserLevel;
    }

    public String getToUserLevelName() {
        return toUserLevelName;
    }

    public void setToUserLevelName(String toUserLevelName) {
        this.toUserLevelName = toUserLevelName;
    }

    public String getToUserLevelColor() {
        return toUserLevelColor;
    }

    public void setToUserLevelColor(String toUserLevelColor) {
        this.toUserLevelColor = toUserLevelColor;
    }

    public String getToUserSex() {
        return toUserSex;
    }

    public void setToUserSex(String toUserSex) {
        this.toUserSex = toUserSex;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public boolean isDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public boolean isNewDeleteFlag() {
        return newDeleteFlag;
    }

    public void setNewDeleteFlag(boolean newDeleteFlag) {
        this.newDeleteFlag = newDeleteFlag;
    }

    public ArrayList<BadgeEntity> getBadgeList() {
        return badgeList;
    }

    public void setBadgeList(ArrayList<BadgeEntity> badgeList) {
        this.badgeList = badgeList;
    }
}
