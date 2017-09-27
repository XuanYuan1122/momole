package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by yi on 2017/9/22.
 */

public class CommentV2Entity implements Parcelable {
    private String commentId;
    private String content;
    private UserTopEntity createUser;
    private ArrayList<Image> images;
    private String createTime;
    private int likes;
    private boolean like;
    private ArrayList<CommentV2SecEntity> hotComments;
    private int comments;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<CommentV2Entity> CREATOR = new Parcelable.Creator<CommentV2Entity>() {
        @Override
        public CommentV2Entity createFromParcel(Parcel in) {
            CommentV2Entity entity = new CommentV2Entity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            entity.commentId = bundle.getString("commentId");
            entity.content = bundle.getString("content");
            entity.createUser = bundle.getParcelable("createUser");
            entity.createTime = bundle.getString("createTime");
            entity.images = bundle.getParcelableArrayList("images");
            entity.hotComments = bundle.getParcelableArrayList("hotComments");
            entity.like = bundle.getBoolean("like");
            entity.comments = bundle.getInt("comments");
            entity.likes = bundle.getInt("likes");
            return entity;
        }

        @Override
        public CommentV2Entity[] newArray(int size) {
            return new CommentV2Entity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("commentId",commentId);
        bundle.putString("content",content);
        bundle.putParcelable("createUser",createUser);
        bundle.putString("createTime", createTime);
        bundle.putParcelableArrayList("images",images);
        bundle.putParcelableArrayList("hotComments",hotComments);
        bundle.putBoolean("like",like);
        bundle.putInt("comments",comments);
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

    public ArrayList<CommentV2SecEntity> getHotComments() {
        return hotComments;
    }

    public void setHotComments(ArrayList<CommentV2SecEntity> hotComments) {
        this.hotComments = hotComments;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
