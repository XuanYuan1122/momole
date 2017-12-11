package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yi on 2016/12/15.
 */

public class PersonalMainEntity {

    @SerializedName("level")
    private int level;
    @SerializedName("levelColor")
    private String levelColor;
    @SerializedName("levelName")
    private String levelName;
    @SerializedName("levelScoreEnd")
    private int levelScoreEnd;
    @SerializedName("levelScoreStart")
    private int levelScoreStart;
    @SerializedName("score")
    private int score;
    @SerializedName("signature")
    private String signature;
    @SerializedName("commentCount")
    private int commentCount;
    @SerializedName("commentList")
    private ArrayList<NewCommentEntity> commentList;
    @SerializedName("badgeList")
    private ArrayList<BadgeEntity> badgeList;
    @SerializedName("folderList")
    private ArrayList<ShowFolderEntity> folderList;
    @SerializedName("like")
    private boolean like;
    @SerializedName("picLikes")
    private int picLikes;
    @SerializedName("picPath")
    private String picPath;
    @SerializedName("picAllLikes")
    private int picAllLikes;
    @SerializedName("useArtworkId")
    private String useArtworkId;

    public int getPicAllLikes() {
        return picAllLikes;
    }

    public void setPicAllLikes(int picAllLikes) {
        this.picAllLikes = picAllLikes;
    }

    public String getUseArtworkId() {
        return useArtworkId;
    }

    public void setUseArtworkId(String useArtworkId) {
        this.useArtworkId = useArtworkId;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public int getPicLikes() {
        return picLikes;
    }

    public void setPicLikes(int picLikes) {
        this.picLikes = picLikes;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

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

    public int getLevelScoreEnd() {
        return levelScoreEnd;
    }

    public void setLevelScoreEnd(int levelScoreEnd) {
        this.levelScoreEnd = levelScoreEnd;
    }

    public int getLevelScoreStart() {
        return levelScoreStart;
    }

    public void setLevelScoreStart(int levelScoreStart) {
        this.levelScoreStart = levelScoreStart;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public ArrayList<NewCommentEntity> getCommentList() {
        return commentList;
    }

    public void setCommentList(ArrayList<NewCommentEntity> commentList) {
        this.commentList = commentList;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public ArrayList<BadgeEntity> getBadgeList() {
        return badgeList;
    }

    public void setBadgeList(ArrayList<BadgeEntity> badgeList) {
        this.badgeList = badgeList;
    }

    public ArrayList<ShowFolderEntity> getFolderList() {
        return folderList;
    }

    public void setFolderList(ArrayList<ShowFolderEntity> folderList) {
        this.folderList = folderList;
    }
}
