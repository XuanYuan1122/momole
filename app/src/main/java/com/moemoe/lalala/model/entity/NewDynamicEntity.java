package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * feed流item
 * Created by yi on 2017/9/20.
 */

public class NewDynamicEntity implements Parcelable{
    private String id;// 动态ID
    private UserTopEntity createUser;// 创建人
    private String createTime;// 创建时间
    private long timestamp;
    private String text; // 外层的文字
    private String type;// 类型  DYNAMIC(动态),FOLDER(分享文件夹),ARTICLE(分享文章),RETWEET(转发),MUSIC(音乐),MOVIE(视频),DELETE(删除) PRODUCT MESSAGE
    private JsonObject detail;// 详情
    private String from;
    private String fromSchema;
    private ArrayList<DocTagEntity> tags;
    private int reward;

    private boolean tag;// 是否允许打标签
    private int retweets;// 转发数
    private int comments;// 评论数
    private int likes;// 喜欢数
    private int thumbs;// 点赞数
    private boolean collect;
    private boolean follow;
    private boolean thumb;
    private ArrayList<SimpleUserEntity> thumbUsers;
    private int coins;
    private int surplus;
    private int users;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<NewDynamicEntity> CREATOR = new Parcelable.Creator<NewDynamicEntity>() {
        @Override
        public NewDynamicEntity createFromParcel(Parcel in) {
            NewDynamicEntity entity = new NewDynamicEntity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            entity.id = bundle.getString("id");
            entity.createUser = bundle.getParcelable("createUser");
            entity.createTime = bundle.getString("createTime");
            entity.timestamp = bundle.getLong("timestamp");
            entity.text = bundle.getString("text");
            entity.type = bundle.getString("type");
            entity.detail = new Gson().fromJson(bundle.getString("detail"),JsonObject.class);
            entity.from = bundle.getString("from");
            entity.fromSchema = bundle.getString("fromSchema");
            entity.tags = bundle.getParcelableArrayList("tags");
            entity.thumbUsers = bundle.getParcelableArrayList("thumbUsers");
            entity.tag = bundle.getBoolean("tag");
            entity.collect = bundle.getBoolean("collect");
            entity.follow = bundle.getBoolean("follow");
            entity.thumb = bundle.getBoolean("thumb");
            entity.retweets = bundle.getInt("retweets");
            entity.comments = bundle.getInt("comments");
            entity.likes = bundle.getInt("likes");
            entity.reward = bundle.getInt("reward");
            entity.thumbs = bundle.getInt("thumbs");
            entity.coins = bundle.getInt("coins");
            entity.surplus = bundle.getInt("surplus");
            entity.users = bundle.getInt("users");
            return entity;
        }

        @Override
        public NewDynamicEntity[] newArray(int size) {
            return new NewDynamicEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putParcelable("createUser",createUser);
        bundle.putString("createTime", createTime);
        bundle.putLong("timestamp", timestamp);
        bundle.putString("text",text);
        bundle.putString("type",type);
        bundle.putString("detail",detail.toString());
        bundle.putString("from",from);
        bundle.putString("fromSchema",fromSchema);
        bundle.putParcelableArrayList("tags",tags);
        bundle.putParcelableArrayList("thumbUsers",thumbUsers);
        bundle.putBoolean("tag",tag);
        bundle.putBoolean("collect",collect);
        bundle.putBoolean("follow",follow);
        bundle.putBoolean("thumb",thumb);
        bundle.putInt("retweets",retweets);
        bundle.putInt("comments",comments);
        bundle.putInt("likes",likes);
        bundle.putInt("reward",reward);
        bundle.putInt("thumbs",thumbs);
        bundle.putInt("coins",coins);
        bundle.putInt("surplus",surplus);
        bundle.putInt("users",users);
        parcel.writeBundle(bundle);
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getSurplus() {
        return surplus;
    }

    public void setSurplus(int surplus) {
        this.surplus = surplus;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public ArrayList<SimpleUserEntity> getThumbUsers() {
        return thumbUsers;
    }

    public void setThumbUsers(ArrayList<SimpleUserEntity> thumbUsers) {
        this.thumbUsers = thumbUsers;
    }

    public int getThumbs() {
        return thumbs;
    }

    public void setThumbs(int thumbs) {
        this.thumbs = thumbs;
    }

    public boolean isThumb() {
        return thumb;
    }

    public void setThumb(boolean thumb) {
        this.thumb = thumb;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserTopEntity getCreateUser() {
        return createUser;
    }

    public void setCreateUser(UserTopEntity createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public JsonObject getDetail() {
        return detail;
    }

    public void setDetail(JsonObject detail) {
        this.detail = detail;
    }

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }

    public int getRetweets() {
        return retweets;
    }

    public void setRetweets(int retweets) {
        this.retweets = retweets;
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

    public ArrayList<DocTagEntity> getTags() {
        return tags;
    }

    public void setTags(ArrayList<DocTagEntity> tags) {
        this.tags = tags;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromSchema() {
        return fromSchema;
    }

    public void setFromSchema(String fromSchema) {
        this.fromSchema = fromSchema;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
}
