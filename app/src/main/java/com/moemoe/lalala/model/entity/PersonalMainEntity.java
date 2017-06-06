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
}
