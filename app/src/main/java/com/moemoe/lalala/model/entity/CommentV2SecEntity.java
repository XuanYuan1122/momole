package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by yi on 2017/9/22.
 */

public class CommentV2SecEntity implements Parcelable {

    private String commentId;
    private String content;
    private UserTopEntity createUser;
    private ArrayList<Image> images;
    private String createTime;
    private int likes;
    private boolean like;
    private String commentTo;
    private String commentToName;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<CommentV2SecEntity> CREATOR = new Parcelable.Creator<CommentV2SecEntity>() {
        @Override
        public CommentV2SecEntity createFromParcel(Parcel in) {
            CommentV2SecEntity entity = new CommentV2SecEntity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            entity.commentId = bundle.getString("commentId");
            entity.content = bundle.getString("content");
            entity.commentTo = bundle.getString("commentTo");
            entity.createUser = bundle.getParcelable("createUser");
            entity.createTime = bundle.getString("createTime");
            entity.commentToName = bundle.getString("commentToName");
            entity.images = bundle.getParcelableArrayList("images");
            entity.like = bundle.getBoolean("like");
            entity.likes = bundle.getInt("likes");
            return entity;
        }

        @Override
        public CommentV2SecEntity[] newArray(int size) {
            return new CommentV2SecEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("commentId",commentId);
        bundle.putString("content",content);
        bundle.putString("commentTo",commentTo);
        bundle.putParcelable("createUser",createUser);
        bundle.putString("createTime", createTime);
        bundle.putString("commentToName", commentToName);
        bundle.putParcelableArrayList("images",images);
        bundle.putBoolean("like",like);
        bundle.putInt("likes",likes);
        parcel.writeBundle(bundle);
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserTopEntity getCreateUser() {
        return createUser;
    }

    public void setCreateUser(UserTopEntity createUser) {
        this.createUser = createUser;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getCommentTo() {
        return commentTo;
    }

    public void setCommentTo(String commentTo) {
        this.commentTo = commentTo;
    }

    public String getCommentToName() {
        return commentToName;
    }

    public void setCommentToName(String commentToName) {
        this.commentToName = commentToName;
    }
}
