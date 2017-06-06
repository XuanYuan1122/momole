package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/10.
 */
public class CalendarDayEntity {
    @SerializedName("day")
    private Day day;
    @SerializedName("items")
    private ArrayList<Items> items;

    public class Day{
        @SerializedName("bg")
        private Image bg;
        @SerializedName("day")
        private String day;
        @SerializedName("yesterday")
        private String yesterday;
        @SerializedName("readTime")
        private String readTime;
        @SerializedName("size")
        private int size;
        @SerializedName("week")
        private String week;
        @SerializedName("userName")
        private String userName;
        @SerializedName("userIcon")
        private Image userIcon;

        public String getYesterday() {
            return yesterday;
        }

        public void setYesterday(String yesterday) {
            this.yesterday = yesterday;
        }

        public Image getBg() {
            return bg;
        }

        public void setBg(Image bg) {
            this.bg = bg;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getReadTime() {
            return readTime;
        }

        public void setReadTime(String readTime) {
            this.readTime = readTime;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Image getUserIcon() {
            return userIcon;
        }

        public void setUserIcon(Image userIcon) {
            this.userIcon = userIcon;
        }
    }

    public class Items{
        @SerializedName("comments")
        private int comments;
        @SerializedName("content")
        private String content;
        @SerializedName("docId")
        private String docId;
        @SerializedName("image")
        private Image image;
        @SerializedName("likes")
        private int likes;
        @SerializedName("mark")
        private String mark;
        @SerializedName("schema")
        private String schema;
        @SerializedName("showType")
        private String showType;
        @SerializedName("title")
        private String title;
        @SerializedName("uiName")
        private String uiName;
        @SerializedName("userIcon")
        private Image userIcon;
        @SerializedName("userName")
        private String userName;
        @SerializedName("userId")
        private String userId;
        private String day;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
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

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
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

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        public String getShowType() {
            return showType;
        }

        public void setShowType(String showType) {
            this.showType = showType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUiName() {
            return uiName;
        }

        public void setUiName(String uiName) {
            this.uiName = uiName;
        }

        public Image getUserIcon() {
            return userIcon;
        }

        public void setUserIcon(Image userIcon) {
            this.userIcon = userIcon;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = items;
    }
}
