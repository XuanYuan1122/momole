package com.moemoe.lalala.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/4/21 0021.
 */
public class CalendarDayItem {
    @SerializedName("day")
    private String day;
    @SerializedName("docs")
    private ArrayList<CalendarData> docs;

    public class CalendarData{
        @SerializedName("comments")
        private int comments;
        @SerializedName("content")
        private String content;
        @SerializedName("icon")
        private Image icon;
        @SerializedName("images")
        private ArrayList<Image> images;
        @SerializedName("likes")
        private int likes;
        @SerializedName("mark")
        private String mark;
        @SerializedName("musicName")
        private String musicName;
        @SerializedName("musicUrl")
        private String musicUrl;
        @SerializedName("refId")
        private String refId;
        @SerializedName("schema")
        private String schema;
        @SerializedName("title")
        private String title;
        @SerializedName("ui")
        private String ui;
        @SerializedName("updateTime")
        private String updateTime;
        @SerializedName("userName")
        private String userName;

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

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

        public String getMusicName() {
            return musicName;
        }

        public void setMusicName(String musicName) {
            this.musicName = musicName;
        }

        public String getMusicUrl() {
            return musicUrl;
        }

        public void setMusicUrl(String musicUrl) {
            this.musicUrl = musicUrl;
        }

        public String getRefId() {
            return refId;
        }

        public void setRefId(String refId) {
            this.refId = refId;
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

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<CalendarData> getDocs() {
        return docs;
    }

    public void setDocs(ArrayList<CalendarData> docs) {
        this.docs = docs;
    }
}
