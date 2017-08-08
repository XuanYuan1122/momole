package com.moemoe.lalala.model.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yi on 2017/7/27.
 */

public class NewDocListEntity {

    @SerializedName("id")
    private String id;//item Id
    @SerializedName("createTime")
    private String createTime;//创建时间
    @SerializedName("time")
    private long time;
    @SerializedName("detail")
    private Detail detail;

    public class Detail{
        /**
         *  帖子普通样式:DOC
         *  文件夹更新:FOLLOW_USER_FOLDER
         *  评论帖子:FOLLOW_USER_COMMENT
         *  关注对象关注动态:FOLLOW_USER_FOLLOW
         *  学部推荐:FOLLOW_DEPARTMENT
         *  板报推荐:FOLLOW_BROADCAST
         */
        @SerializedName("type")
        private String type;
        @SerializedName("data")
        private JsonObject data;

        private Object trueData;

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

        public Object getTrueData() {
            return trueData;
        }

        public void setTrueData(Object trueData) {
            this.trueData = trueData;
        }
    }

    public class Doc{

        private String docId;//帖子ID

        private String docFrom;//帖子来源，如热门推荐，为空则不显示

        private String fromSchema;//来源跳转schema,为空则不可跳转

        private Image userIcon;//发帖人头像

        private String userId;//发帖人ID

        private int userLevel;//发帖人等级

        private String userLevelColor;//发帖人等级颜色

        private String userName;//发帖人昵称

        private ArrayList<BadgeEntity> badgeList;//发帖人显示徽章列表

        private String title;//帖子标题

        private String content;//帖子内容

        private ArrayList<DocTagEntity> tags;//帖子标签

        private ArrayList<Image> images;//帖子图片列表，最多3个

        private String schema;//帖子跳转schema

        private int comments;//回复数

        private int likes;//标签点赞总数

        private int eggs;//鸡蛋数

        private boolean throwEgg;//是否扔过鸡蛋

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public String getDocFrom() {
            return docFrom;
        }

        public void setDocFrom(String docFrom) {
            this.docFrom = docFrom;
        }

        public String getFromSchema() {
            return fromSchema;
        }

        public void setFromSchema(String fromSchema) {
            this.fromSchema = fromSchema;
        }

        public Image getUserIcon() {
            return userIcon;
        }

        public void setUserIcon(Image userIcon) {
            this.userIcon = userIcon;
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

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public ArrayList<BadgeEntity> getBadgeList() {
            return badgeList;
        }

        public void setBadgeList(ArrayList<BadgeEntity> badgeList) {
            this.badgeList = badgeList;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public ArrayList<DocTagEntity> getTags() {
            return tags;
        }

        public void setTags(ArrayList<DocTagEntity> tags) {
            this.tags = tags;
        }

        public ArrayList<Image> getImages() {
            return images;
        }

        public void setImages(ArrayList<Image> images) {
            this.images = images;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public int getEggs() {
            return eggs;
        }

        public void setEggs(int eggs) {
            this.eggs = eggs;
        }

        public boolean isThrowEgg() {
            return throwEgg;
        }

        public void setThrowEgg(boolean throwEgg) {
            this.throwEgg = throwEgg;
        }
    }

    public class FollowFolder{

        private BagDirEntity folder;//文件夹信息

        private Image userIcon;//文件夹拥有者头像

        private String userId;//文件夹拥有者ID

        private int userLevel;//文件夹拥有者等级

        private String userLevelColor;//文件夹拥有者等级颜色

        private String userName;//文件夹拥有者昵称

        private ArrayList<BadgeEntity> badgeList;//文件夹拥有者显示徽章列表

        private String extra;//更新说明

        private String  extraColorContent;//更新说明高亮部分

        public Image getUserIcon() {
            return userIcon;
        }

        public void setUserIcon(Image userIcon) {
            this.userIcon = userIcon;
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

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public ArrayList<BadgeEntity> getBadgeList() {
            return badgeList;
        }

        public void setBadgeList(ArrayList<BadgeEntity> badgeList) {
            this.badgeList = badgeList;
        }

        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        public String getExtraColorContent() {
            return extraColorContent;
        }

        public void setExtraColorContent(String extraColorContent) {
            this.extraColorContent = extraColorContent;
        }

        public BagDirEntity getFolder() {
            return folder;
        }

        public void setFolder(BagDirEntity folder) {
            this.folder = folder;
        }
    }

    public class FollowComment{
        private String docId;//被评论帖子ID

        private Image userIcon;//评论者头像

        private String userId;//评论者ID

        private int userLevel;//评论者等级

        private String userLevelColor;//评论者等级颜色

        private String userName;//评论者昵称

        private ArrayList<BadgeEntity> badgeList;//评论者显示徽章列表

        private String extra;//更新说明

        private String  extraColorContent;//更新说明高亮部分

        private Image docIcon;//被评论帖子icon

        private String docTitle;//被评论帖子的标题

        private String docContent;//被评论帖子内容

        private String updateTime;//被评论帖子更新时间

        private int likes;//被评论帖子标签点赞数

        private int comments;//被评论帖子回复数

        private String schema;//被评论帖子跳转schema

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public Image getUserIcon() {
            return userIcon;
        }

        public void setUserIcon(Image userIcon) {
            this.userIcon = userIcon;
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

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public ArrayList<BadgeEntity> getBadgeList() {
            return badgeList;
        }

        public void setBadgeList(ArrayList<BadgeEntity> badgeList) {
            this.badgeList = badgeList;
        }

        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        public Image getDocIcon() {
            return docIcon;
        }

        public void setDocIcon(Image docIcon) {
            this.docIcon = docIcon;
        }

        public String getDocTitle() {
            return docTitle;
        }

        public void setDocTitle(String docTitle) {
            this.docTitle = docTitle;
        }

        public String getDocContent() {
            return docContent;
        }

        public void setDocContent(String docContent) {
            this.docContent = docContent;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        public String getExtraColorContent() {
            return extraColorContent;
        }

        public void setExtraColorContent(String extraColorContent) {
            this.extraColorContent = extraColorContent;
        }
    }

    public class FollowUser{

        private Image userIcon;//关注人头像

        private String userId;//关注人ID

        private int userLevel;//关注人等级

        private String userLevelColor;//关注人等级颜色

        private String userName;//关注人昵称

        private ArrayList<BadgeEntity> badgeList;//评关注人显示徽章列表

        private String extra;//关注内容

        private String extraColorContent;//关注内容高亮部分（目前是关注人昵称）

        private String extraId;//关注内容高亮部分ID(目前是关注人ID)

        public Image getUserIcon() {
            return userIcon;
        }

        public void setUserIcon(Image userIcon) {
            this.userIcon = userIcon;
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

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public ArrayList<BadgeEntity> getBadgeList() {
            return badgeList;
        }

        public void setBadgeList(ArrayList<BadgeEntity> badgeList) {
            this.badgeList = badgeList;
        }

        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        public String getExtraColorContent() {
            return extraColorContent;
        }

        public void setExtraColorContent(String extraColorContent) {
            this.extraColorContent = extraColorContent;
        }

        public String getExtraId() {
            return extraId;
        }

        public void setExtraId(String extraId) {
            this.extraId = extraId;
        }
    }

    public class FollowDepartment{

        private String docId;//帖子ID

        private Image docIcon;//帖子ICON

        private String docFrom;//帖子来源，如影音部，为空则不显示

        private String fromSchema;//来源跳转,为空则不可跳转

        private Image userIcon;//发帖人头像

        private String userId;//发帖人ID

        private String userName;//发帖人昵称

        private String title;//帖子标题

        private String content;//帖子内容

        private int likes;//标签点赞数

        private int comments;//评论数

        private String schema;//帖子跳转schema

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public Image getDocIcon() {
            return docIcon;
        }

        public void setDocIcon(Image docIcon) {
            this.docIcon = docIcon;
        }

        public String getDocFrom() {
            return docFrom;
        }

        public void setDocFrom(String docFrom) {
            this.docFrom = docFrom;
        }

        public String getFromSchema() {
            return fromSchema;
        }

        public void setFromSchema(String fromSchema) {
            this.fromSchema = fromSchema;
        }

        public Image getUserIcon() {
            return userIcon;
        }

        public void setUserIcon(Image userIcon) {
            this.userIcon = userIcon;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }
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

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
