package com.moemoe.lalala.data;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/10.
 */
@Table(name = "t_doc_list_data_v1.0")
public class DocListBean {
    @Column(name = "id",isId = true,autoGen = false)
    public String id;
    @Column(name = "json")
    public String json;

    @SerializedName("desc")
    private DocDetail desc;
    @SerializedName("tags")
    private ArrayList<DocTag> tags;
    @SerializedName("userIcon")
    private Image userIcon;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userLevel")
    private int userLevel;
    @SerializedName("userLevelColor")
    private String userLevelColor;
    @SerializedName("userLevelName")
    private String userLevelName;
    @SerializedName("userName")
    private String userName;
    @SerializedName("userSex")
    private String userSex;
    @SerializedName("updateTime")
    private String updateTime;

    public class DocDetail{
        @SerializedName("comments")
        private int comments;
        @SerializedName("content")
        private String content;
        @SerializedName("id")
        private String id;
        @SerializedName("images")
        private ArrayList<Image> images;
        @SerializedName("likes")
        private int likes;
        @SerializedName("music")
        private DocMusic music;
        @SerializedName("title")
        private String title;
        @SerializedName("schema")
        private String schema;

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public DocMusic getMusic() {
            return music;
        }

        public void setMusic(DocMusic music) {
            this.music = music;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }
    }

    public class DocMusic{
        @SerializedName("cover")
        private Image cover;
        @SerializedName("name")
        private String name;
        @SerializedName("timestamp")
        private int timestamp;
        @SerializedName("url")
        private String url;

        public Image getCover() {
            return cover;
        }

        public void setCover(Image cover) {
            this.cover = cover;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserLevelColor() {
        return userLevelColor;
    }

    public void setUserLevelColor(String userLevelColor) {
        this.userLevelColor = userLevelColor;
    }

    public String getUserLevelName() {
        return userLevelName;
    }

    public void setUserLevelName(String userLevelName) {
        this.userLevelName = userLevelName;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public Image getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(Image userIcon) {
        this.userIcon = userIcon;
    }

    public ArrayList<DocTag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<DocTag> tags) {
        this.tags = tags;
    }

    public DocDetail getDesc() {
        return desc;
    }

    public void setDesc(DocDetail desc) {
        this.desc = desc;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
