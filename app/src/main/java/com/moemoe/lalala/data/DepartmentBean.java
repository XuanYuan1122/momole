package com.moemoe.lalala.data;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/8/11 0011.
 */
@Table(name = "t_department_doc_list_data_v1.0")
public class DepartmentBean {
    @Column(name = "id",isId = true,autoGen = false)
    public String id;
    @Column(name = "json")
    public String json;

    @SerializedName("before")
    private String before;
    @SerializedName("list")
    private ArrayList<DepartmentDoc> list;

    public class DepartmentDoc{
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
        @SerializedName("musicUrl")
        private String musicUrl;
        @SerializedName("musicName")
        private String musicName;
        @SerializedName("mark")
        private String mark;
        @SerializedName("schema")
        private String schema;
        @SerializedName("title")
        private String title;
        @SerializedName("ui")
        private String ui;
        @SerializedName("uiTitle")
        private String uiTitle;
        @SerializedName("username")
        private String username;
        @SerializedName("updateTime")
        private String updateTime;


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
