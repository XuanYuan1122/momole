package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 *
 * Created by Haru on 2016/8/11 0011.
 */
public class DepartmentEntity {

    private String before;
    private ArrayList<DepartmentDoc> list;

    public class DepartmentDoc{
        private int comments;
        private String content;
        private Image icon;
        private ArrayList<Image> images;
        private int likes;
        private String musicUrl;
        private String musicName;
        private String mark;
        private String schema;
        private String title;
        private String ui;
        private String uiTitle;
        private String username;
        private String updateTime;
        private String userId;
        private int readNum;
        private String headIcon;
        private int coin;
        private int score;

        public int getCoin() {
            return coin;
        }

        public void setCoin(int coin) {
            this.coin = coin;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getReadNum() {
            return readNum;
        }

        public void setReadNum(int readNum) {
            this.readNum = readNum;
        }

        public String getHeadIcon() {
            return headIcon;
        }

        public void setHeadIcon(String headIcon) {
            this.headIcon = headIcon;
        }

        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Image getIcon() {
            return icon;
        }

        public void setIcon(Image icon) {
            this.icon = icon;
        }

        public ArrayList<Image> getImages() {
            return images;
        }

        public void setImages(ArrayList<Image> images) {
            this.images = images;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public String getMusicUrl() {
            return musicUrl;
        }

        public void setMusicUrl(String musicUrl) {
            this.musicUrl = musicUrl;
        }

        public String getMusicName() {
            return musicName;
        }

        public void setMusicName(String musicName) {
            this.musicName = musicName;
        }

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUi() {
            return ui;
        }

        public void setUi(String ui) {
            this.ui = ui;
        }

        public String getUiTitle() {
            return uiTitle;
        }

        public void setUiTitle(String uiTitle) {
            this.uiTitle = uiTitle;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public ArrayList<DepartmentDoc> getList() {
        return list;
    }

    public void setList(ArrayList<DepartmentDoc> list) {
        this.list = list;
    }
}
