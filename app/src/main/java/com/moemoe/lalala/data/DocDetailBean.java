package com.moemoe.lalala.data;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/10.
 */
@Table(name = "t_doc_v1.0")
public class DocDetailBean {
    @Column(name = "id",isId = true,autoGen = false)
    @SerializedName("id")
    private String id;
    @Column(name = "json")
    public String json;
    @SerializedName("coin")
    private int coin;
    @SerializedName("coinDetails")
    private ArrayList<Detail> coinDetails;
    @SerializedName("coinPays")
    private int coinPays;
    @SerializedName("comments")
    private int comments;
    @SerializedName("createTime")
    private String createTime;
    @SerializedName("details")
    private ArrayList<Detail> details;
    @SerializedName("favoriteFlag")
    private boolean favoriteFlag;
    @SerializedName("likes")
    private int likes;
    @SerializedName("share")
    private ShareInfo share;
    @SerializedName("tags")
    private ArrayList<DocTag> tags;
    @SerializedName("title")
    private String title;
    @SerializedName("updateTime")
    private String updateTime;
    @SerializedName("userIcon")
    private String userIcon;
    @SerializedName("userIconH")
    private int userIconH;
    @SerializedName("userIconW")
    private int userIconW;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userLevel")
    private int userLevel;
    @SerializedName("userLevelColor")
    private String userLevelColor;
    @SerializedName("userLevelName")
    private String userLevelName;
    @SerializedName("userScore")
    private int userScore;
    @SerializedName("userSex")
    private String userSex;
    @SerializedName("userName")
    private String userName;

    public class Detail<T>{
        @SerializedName("type")
        private String type;
        @SerializedName("data")
        private JsonObject data;

        private T trueData;

        public T getTrueData() {
            return trueData;
        }

        public void setTrueData(T trueData) {
            this.trueData = trueData;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public JsonObject getData() {
            return data;
        }

        public void setData(JsonObject data) {
            this.data = data;
        }
    }

    public class ShareInfo{
        @SerializedName("icon")
        private String icon;
        @SerializedName("title")
        private String title;
        @SerializedName("desc")
        private String desc;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public class DocMusic{
        private Image cover;

        private String name;

        private int timestamp;

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

    public class DocLink{
        private Image icon;

        private String name;

        private String url;

        public Image getIcon() {
            return icon;
        }

        public void setIcon(Image icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public class DocGroupLink{

        private ArrayList<DocGroupLinkDetail> items;

        public ArrayList<DocGroupLinkDetail> getItems() {
            return items;
        }

        public void setItems(ArrayList<DocGroupLinkDetail> items) {
            this.items = items;
        }

        public class DocGroupLinkDetail{
            private   String name;
            private  String url;
            private  String color;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public ArrayList<Detail> getCoinDetails() {
        return coinDetails;
    }

    public void setCoinDetails(ArrayList<Detail> coinDetails) {
        this.coinDetails = coinDetails;
    }

    public int getCoinPays() {
        return coinPays;
    }

    public void setCoinPays(int coinPays) {
        this.coinPays = coinPays;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public ArrayList<Detail> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<Detail> details) {
        this.details = details;
    }

    public boolean isFavoriteFlag() {
        return favoriteFlag;
    }

    public void setFavoriteFlag(boolean favoriteFlag) {
        this.favoriteFlag = favoriteFlag;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public ShareInfo getShare() {
        return share;
    }

    public void setShare(ShareInfo share) {
        this.share = share;
    }

    public ArrayList<DocTag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<DocTag> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public int getUserIconH() {
        return userIconH;
    }

    public void setUserIconH(int userIconH) {
        this.userIconH = userIconH;
    }

    public int getUserIconW() {
        return userIconW;
    }

    public void setUserIconW(int userIconW) {
        this.userIconW = userIconW;
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

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
